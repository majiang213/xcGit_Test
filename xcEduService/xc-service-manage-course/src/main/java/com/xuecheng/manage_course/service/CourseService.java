package com.xuecheng.manage_course.service;

import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.model.response.ResponseResult;

public interface CourseService {

    Teachplan findTeachplanList(String courseId);

    ResponseResult addTeachplan(Teachplan teachplan);
}
