package com.xuecheng.framework.domain.cms.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class QueryPageRequest {

    // 站点id
    @ApiModelProperty("站点id")
    private String siteId;

    // 页面id
    @ApiModelProperty("页面id")
    private String pageId;

    // 页面名称
    @ApiModelProperty("页面名称")
    private String pageName;

    // 页面别名
    @ApiModelProperty("页面别名")
    private String pageAliase;

    // 模板id
    @ApiModelProperty("模板id")
    private String template;
}
