package com.xuecheng.api.course;

import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value="course管理接口",description = "课程管理")
public interface CourseControllerApi {

    @ApiOperation("课程计划列表分级查询")
    Teachplan findTeachplanList(String courseId);

    @ApiOperation("添加课程计划节点")
    ResponseResult addTeachplan(Teachplan teachplan);
}
