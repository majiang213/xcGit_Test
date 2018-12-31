package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;

public interface CmsPageService {
    QueryResponseResult findList( int page, int size, QueryPageRequest queryPageRequest);

    CmsPageResult add(CmsPage cmsPage);

    CmsPage findOne(CmsPage cmsPage);

    CmsPageResult findOne(String id);

    CmsPageResult update(String id,CmsPage cmsPage);

    CmsPageResult delete(String id);

    String getPageHtml(String pageId);

    ResponseResult post(String pageId);
}
