package com.xuecheng.manage_cms.service;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import com.xuecheng.manage_cms.dao.CmsSIteRepository;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

@Service
public class PageService {

    @Autowired
    private CmsPageRepository cmsPageRepository;

    @Autowired
    private CmsSIteRepository cmsSIteRepository;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFSBucket gridFSBucket;

    public void postPage(String pageId){
        // 通过页面id进行页面查询
        Optional<CmsPage> cmsPageOptional = cmsPageRepository.findById(pageId);
        if (!cmsPageOptional.isPresent()){
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        // 获取页面信息
        CmsPage cmsPage = cmsPageOptional.get();
        // 通过页面中的站点id查询站点信息
        CmsSite cmsSite = this.findOneById(cmsPage.getSiteId());
        // 获得该发布页面的实际物理路径
        String pagePhysicalPath = cmsSite.getSitePhysicalPath() + cmsPage.getPagePhysicalPath() + cmsPage.getPageName();
        // 从GridFs中下载静态页面,返回包含文件内容的流
        InputStream pageContent = this.findPageContent(cmsPage.getHtmlFileId());

        try {
            // 拷贝到文件的物理路径中
            IOUtils.copy(pageContent,new FileOutputStream(pagePhysicalPath));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 通过站点id查询MongoDB数据库
     * @param siteId
     * @return
     */
    public CmsSite findOneById(String siteId){
        Optional<CmsSite> optional = cmsSIteRepository.findById(siteId);

        if (!optional.isPresent()){
            ExceptionCast.cast(CmsCode.CMS_SITE_DATAISNULL);
        }
        return optional.get();
    }

    /**
     * 进行文件下载方法
     * @param htmlFileId 传入GridFs的文件id
     * @return 返回包含文件内容的字节输入流
     */
    public InputStream findPageContent(String htmlFileId){
        GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(htmlFileId)));
        GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
        GridFsResource gridFsResource = new GridFsResource(gridFSFile,gridFSDownloadStream);
        try {
            return  gridFsResource.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
