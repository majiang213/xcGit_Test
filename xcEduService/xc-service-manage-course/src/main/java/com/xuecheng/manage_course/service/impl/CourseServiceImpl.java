package com.xuecheng.manage_course.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.CourseMarket;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.CourseCode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.dao.*;
import com.xuecheng.manage_course.service.CourseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private TeachplanMapper teachplanMapper;

    @Autowired
    private TeachplanRepository teachplanRepository;

    @Autowired
    private CourseBaseRepository courseBaseRepository;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private CourseMarketRepository courseMarketRepository;

    /**
     * 查询当前课程的课程计划
     *
     * @param courseId 课程id
     * @return 课程计划带有层级结构的菜单, 由TeachplanNode对象表示
     */
    @Override
    public Teachplan findTeachplanList(String courseId) {
        return teachplanMapper.findTeachplanList(courseId);
    }

    /**
     * 添加课程计划树的节点
     *
     * @param teachplan 节点对象
     * @return 响应码
     */
    @Override
    @Transactional
    public ResponseResult addTeachplan(Teachplan teachplan) {
        // 验证节点对象中的参数是否齐全
        if (teachplan == null || StringUtils.isEmpty(teachplan.getCourseid()) || StringUtils.isEmpty(teachplan.getPname())) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        // 判断该节点的父节点是否为空,如果为空则代表该节点为二级节点
        if (StringUtils.isEmpty(teachplan.getParentid())) {
            // 获取该课程计划的根节点
            String teachplanRootId = getTeachplanRoot(teachplan.getCourseid());
            if (StringUtils.isEmpty(teachplanRootId)) {// 如果父节点id返回为null则表示参数非法
                ExceptionCast.cast(CommonCode.INVALID_PARAM);
            }
            // 设置该节点的父节点id为跟节点id
            teachplan.setParentid(teachplanRootId);
            // 设置节点等级为2
            teachplan.setGrade("2");
        } else {
            // 父节点id不为空,则设置该节点的等级为3级
            teachplan.setGrade("3");
        }
        teachplanRepository.save(teachplan);
        return new ResponseResult(CommonCode.SUCCESS);
    }


    /**
     * 获取课程根节点id,如果没有则创建根节点,并返回id
     *
     * @param courseId 课程id
     * @return 课程在teachplan表中的根节点id
     */
    @Transactional
    public String getTeachplanRoot(String courseId) {
        // 验证课程id是否正确
        Optional<CourseBase> courseBaseOptional = courseBaseRepository.findById(courseId);
        if (!courseBaseOptional.isPresent()) {
            return null;
        }
        // 获取课程对象
        CourseBase courseBase = courseBaseOptional.get();
        // 通过课程id和父节点id查询该课程的课程计划跟节点
        Teachplan teachplan = teachplanRepository.findByCourseidAndParentid(courseBase.getId(), "0");
        // 判断该课程计划的跟节点是否为空,为空则为其创建跟节点
        if (teachplan == null) {
            teachplan = new Teachplan();
            teachplan.setPname(courseBase.getName());
            teachplan.setCourseid(courseBase.getId());
            teachplan.setGrade("1");// 节点等级为1,表示根节点
            teachplan.setParentid("0");// 父节点id为0
            teachplan.setStatus("0");// 表示未发布

            teachplanRepository.save(teachplan);// 保存进数据库
            // 保存后,返回跟节点id
            return teachplan.getId();
        }
        // 根节点不为空,保存后返回根节点id
        return teachplan.getId();
    }

    /**
     * 分页查询方法
     *
     * @param page              查询页数
     * @param size              每页大小
     * @param courseListRequest 查询条件参数
     * @return 查询结果对象
     */
    @Override
    public QueryResponseResult findCourseList(int page, int size, CourseListRequest courseListRequest) {
        // 分页查询开始
        PageHelper.startPage(page, size);
        // 执行查询方法
        Page<CourseInfo> courseList = courseMapper.findCourseList();
        // 创建查询结果
        QueryResult queryResult = new QueryResult();
        queryResult.setList(courseList.getResult());
        queryResult.setTotal(courseList.getTotal());
        // 返回查询响应结果对象
        return new QueryResponseResult(CommonCode.SUCCESS, queryResult);
    }

    /**
     * 课程基本信息新增
     * @param courseBase 课程基本信息对象
     * @return 响应码
     */
    @Override
    public ResponseResult addCourse(CourseBase courseBase) {

        if (StringUtils.isEmpty(courseBase.getName())){
            ExceptionCast.cast(CourseCode.COURSE_NAMEISNULL);
        }
        if (StringUtils.isEmpty(courseBase.getGrade())){
            ExceptionCast.cast(CourseCode.COURSE_GRADISNULL);
        }
        if (StringUtils.isEmpty(courseBase.getStudymodel())){
            ExceptionCast.cast(CourseCode.COURSE_STUDYMODEL);
        }

        courseBaseRepository.save(courseBase);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 通过课程id查询课程基本信息对象并返回
     * @param courseId
     * @return
     */
    @Override
    public CourseBase getCourseBaseById(String courseId) {

        Optional<CourseBase> courseBaseOptional = courseBaseRepository.findById(courseId);
        if (!courseBaseOptional.isPresent()){
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        return courseBaseOptional.get();
    }

    /**
     * 更新课程基本信息对象
     * @param id 课程id
     * @param courseBase 需要更新进数据库的课程基本信息
     * @return 操作码
     */
    @Override
    public ResponseResult updateCourseBase(String id, CourseBase courseBase) {
       if (StringUtils.isEmpty(id)){
           ExceptionCast.cast(CommonCode.INVALID_PARAM);
       }
       if (courseBase == null){
           ExceptionCast.cast(CommonCode.INVALID_PARAM);
       }

       courseBaseRepository.save(courseBase);

       return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 通过课程id查询课程的营销信息并返回
     * @param courseId 课程id
     * @return
     */
    @Override
    public CourseMarket getCourseMarketById(String courseId) {
        Optional<CourseMarket> courseMarketOptional = courseMarketRepository.findById(courseId);

        if (courseMarketOptional.isPresent()){
            return courseMarketOptional.get();
        }
        return null;
    }

    /**
     * 更新课程的营销信息,如果课程没有营销信息则添加
     * @param id 课程id
     * @param courseMarket 课程营销信息对象
     * @return
     */
    @Override
    public ResponseResult updateCourseMarket(String id, CourseMarket courseMarket) {
        if (StringUtils.isEmpty(id)){
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        if (courseMarket == null){
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }

        if (!id.equals(courseMarket.getId())){
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        // 进行更新
        courseMarketRepository.save(courseMarket);

        return new ResponseResult(CommonCode.SUCCESS);
    }
}
