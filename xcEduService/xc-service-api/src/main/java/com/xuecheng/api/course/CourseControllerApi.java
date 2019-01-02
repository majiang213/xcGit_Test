package com.xuecheng.api.course;

import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.CourseMarket;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "course管理接口", description = "课程管理")
public interface CourseControllerApi {

    @ApiOperation("课程计划列表分级查询")
    Teachplan findTeachplanList(String courseId);

    @ApiOperation("添加课程计划节点")
    ResponseResult addTeachplan(Teachplan teachplan);

    @ApiOperation("课程列表查询")
    QueryResponseResult findCourseList(int page, int size, CourseListRequest courseListRequest);

    @ApiOperation("新建课程")
    ResponseResult addCourse(CourseBase courseBase);


    @ApiOperation("获取课程基础信息")
    CourseBase getCourseBaseById(String courseId) throws RuntimeException;

    @ApiOperation("更新课程基础信息")
    ResponseResult updateCourseBase(String id,CourseBase courseBase);

    @ApiOperation("获取课程营销信息")
    CourseMarket getCourseMarketById(String courseId);

    @ApiOperation("更新课程营销信息")
    ResponseResult updateCourseMarket(String id,CourseMarket courseMarket);
}
