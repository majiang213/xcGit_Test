package com.xuecheng.manage_course.service;

import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.CourseMarket;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;

public interface CourseService {

    Teachplan findTeachplanList(String courseId);

    ResponseResult addTeachplan(Teachplan teachplan);

    QueryResponseResult findCourseList(int page, int size, CourseListRequest courseListRequest);

    ResponseResult addCourse(CourseBase courseBase);

    CourseBase getCourseBaseById(String courseId) throws RuntimeException;

    ResponseResult updateCourseBase(String id, CourseBase courseBase);

    public CourseMarket getCourseMarketById(String courseId);

    public ResponseResult updateCourseMarket(String id, CourseMarket courseMarket);


}
