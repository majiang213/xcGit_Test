package com.xuecheng.api.cms;

import com.xuecheng.framework.domain.cms.CmsConfig;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "cms配置管理接口",description = "cms配置管理接口,提供cms数据模型的查询,管理")
public interface CmsConfigControllerApi {

    @ApiOperation("通过id查询CMS配置")
    CmsConfig getModel(String id);
}
