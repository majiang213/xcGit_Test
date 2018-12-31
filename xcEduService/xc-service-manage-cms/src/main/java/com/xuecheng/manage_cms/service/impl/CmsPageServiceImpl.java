package com.xuecheng.manage_cms.service.impl;

import com.alibaba.fastjson.JSON;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.config.RabbitMQConfig;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import com.xuecheng.manage_cms.dao.CmsTemplateRepository;
import com.xuecheng.manage_cms.service.CmsPageService;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class CmsPageServiceImpl implements CmsPageService {

    @Autowired
    private CmsPageRepository cmsPageRepository;

    @Autowired
    private CmsTemplateRepository cmsTemplateRepository;

    @Autowired
    private RestTemplate restTemplate;// 客户端发送异步请求

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFSBucket gridFSBucket;// 用于打开GridFs下载流使用

    @Autowired
    private RabbitTemplate rabbitTemplate;


    /**
     * 分页条件查询
     *
     * @param page             分页查询页码从0开始
     * @param size             每页大小
     * @param queryPageRequest 查询条件参数 查询条件为 站点id 页面名称 别名,其中别名为模糊匹配,其余为精确匹配
     * @return
     */
    @Override
    public QueryResponseResult findList(int page, int size, QueryPageRequest queryPageRequest) {
        // 查询条件对象为null则创建一个空的查询条件对象
        if (queryPageRequest == null) {
            queryPageRequest = new QueryPageRequest();
        }
        // 创建封装查询条件信息的实体类
        CmsPage cmsPage = new CmsPage();
        // 构建条件匹配器对象
        ExampleMatcher exampleMatcher = ExampleMatcher.matching().
                // 设置某字段的匹配条件,传入字段名,匹配方式
                        withMatcher("pageAliase", ExampleMatcher.GenericPropertyMatchers.contains());
        // 判断查询条件中是否有站点id
        if (StringUtils.isNotEmpty(queryPageRequest.getSiteId())) {
            cmsPage.setSiteId(queryPageRequest.getSiteId());
        }
        // 判断查询条件中是否有模板id
        if (StringUtils.isNotEmpty(queryPageRequest.getTemplate())) {
            cmsPage.setPageName(queryPageRequest.getTemplate());
        }
        // 判断查询条件中是否有别名
        if (StringUtils.isNotEmpty(queryPageRequest.getPageAliase())) {
            cmsPage.setPageAliase(queryPageRequest.getPageAliase());
        }
        // 构建查询条件,传入条件信息实体类,会将每条实体类中不为null的属性的值作为查询条件,默认匹配方式为等于
        // 传入条件匹配器
        Example<CmsPage> example = Example.of(cmsPage, exampleMatcher);
        if (page < 0) {
            page = 0;
        } else {
            page -= 1;
        }
        if (size <= 0) {
            size = 10;
        }
        Page<CmsPage> pages = cmsPageRepository.findAll(example, PageRequest.of(page, size));
        // 创建查询结果对象
        QueryResult queryResult = new QueryResult();
        // 封装分页内容
        queryResult.setList(pages.getContent());
        // 封装总记录数
        queryResult.setTotal(pages.getTotalElements());
        return new QueryResponseResult(CommonCode.SUCCESS, queryResult);
    }


    /**
     * 通过页面名称,站点id,映射路径,查询单个页面信息
     *
     * @param cmsPage
     * @return
     */
    public CmsPage findOne(CmsPage cmsPage) {
        // 传入参数为null则抛出异常
        if (cmsPage == null) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        return cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath
                (cmsPage.getPageName(), cmsPage.getSiteId(), cmsPage.getPageWebPath());
    }

    /**
     * 添加页面
     *
     * @param cmsPage
     * @return
     */
    @Override
    public CmsPageResult add(CmsPage cmsPage) {
        // 判断页面是否存在
        CmsPage one = findOne(cmsPage);
        // 不等于null则代表页面已存在,抛出页面已存在异常
        if (one != null) {
            ExceptionCast.cast(CmsCode.CMS_ADDPAGE_EXISTSNAME);
        }
        // 使数据库自动生成主键
        cmsPage.setPageId(null);
        one = cmsPageRepository.save(cmsPage);
        return new CmsPageResult(CommonCode.SUCCESS, one);
    }

    @Override
    /**
     * 通过id查询单个页面对象
     */
    public CmsPageResult findOne(String id) {
        if (StringUtils.isEmpty(id)) { // id为空则参数非法
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        // 获取包装类
        Optional<CmsPage> cmsPage = cmsPageRepository.findById(id);
        // 判断是否为空
        if (!cmsPage.isPresent()) {// 为空抛出页面不存在异常
            ExceptionCast.cast(CmsCode.CMS_PAGEISNULL);
        }
        return new CmsPageResult(CommonCode.SUCCESS, cmsPage.get());
    }

    @Override
    /**
     * 修改对象
     */
    public CmsPageResult update(String id, CmsPage cmsPage) {
        //根据id查询页面信息
        CmsPage one = this.findOne(id).getCmsPage();
        if (one != null) {
            //更新模板id
            one.setTemplateId(cmsPage.getTemplateId());
            //更新所属站点
            one.setSiteId(cmsPage.getSiteId());
            //更新页面别名
            one.setPageAliase(cmsPage.getPageAliase());
            //更新页面名称
            one.setPageName(cmsPage.getPageName());
            //更新访问路径
            one.setPageWebPath(cmsPage.getPageWebPath());
            //更新物理路径
            one.setPagePhysicalPath(cmsPage.getPagePhysicalPath());
            //更新数据Url
            one.setDataUrl(cmsPage.getDataUrl());
            //执行更新
            CmsPage save = cmsPageRepository.save(one);
            if (save != null) {
                //返回成功
                CmsPageResult cmsPageResult = new CmsPageResult(CommonCode.SUCCESS, save);
                return cmsPageResult;
            }
        }
        //返回失败
        return new CmsPageResult(CommonCode.FAIL, null);
    }

    @Override
    /**
     * 通过id删除页面
     */
    public CmsPageResult delete(String id) {
        CmsPageResult cmsPageResult = findOne(id);

        if (cmsPageResult.getCmsPage() != null) {
            cmsPageRepository.deleteById(id);
            return cmsPageResult;
        }
        return new CmsPageResult(CommonCode.FAIL, null);
    }

    /**
     * 静态页面生成,返回静态页面的字符串
     *
     * @param pageId
     * @return
     */
    @Override
    public String getPageHtml(String pageId) {
        Map model = getModel(pageId);
        if (model == null) {
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAISNULL);
        }
        String template = getTemplate(pageId);
        if (template == null) {
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }
        String html = generatedHtml(model, template);
        if (html == null) {
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_HTMLISNULL);
        }
        return html;
    }

    /**
     * 访问数据url获得数据模型
     *
     * @param pageId
     * @return
     */
    private Map getModel(String pageId) {
        // 查找页面对象
        CmsPageResult one = this.findOne(pageId);
        CmsPage cmsPage = one.getCmsPage();
        // 判断dataUrl是否为空
        if (StringUtils.isEmpty(cmsPage.getDataUrl())) {
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAURLISNULL);
        }
        // 获得数据Url,发送http请求,获得数据模型
        ResponseEntity<Map> entity = restTemplate.getForEntity(cmsPage.getDataUrl(), Map.class);
        // 返回数据模型
        return entity.getBody();
    }

    /**
     * 获得模板的字符串
     *
     * @param pageId
     * @return 模板内容的字符串
     */
    private String getTemplate(String pageId) {
        // 获取页面对象
        CmsPageResult one = this.findOne(pageId);
        CmsPage cmsPage = one.getCmsPage();
        if (StringUtils.isEmpty(cmsPage.getTemplateId())) {
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }
        // 获得模板对象
        Optional<CmsTemplate> optional = cmsTemplateRepository.findById(cmsPage.getTemplateId());
        // 判断该模板对象是否存在
        if (optional.isPresent()) {
            // 通过模板对象的fileId查询gridFs中对应的模板文件信息对象
            GridFSFile gridFSFile = gridFsTemplate.
                    findOne(Query.query(Criteria.where("_id").is(optional.get().getTemplateFileId())));
            // 打开文件下载流
            GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
            // 获取gridFsResource文件源对象,传入文件信息对象和文件下载流
            GridFsResource gridFsResource = new GridFsResource(gridFSFile, gridFSDownloadStream);

            try {
                // 获得文件下载输入流中的数据,并将其转换为String字符串,并指定字符集
                return IOUtils.toString(gridFsResource.getInputStream(), "UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 执行页面静态化
     * @param model 数据模型
     * @param ftl 模板内容
     * @return 静态化完成的字符串
     */
    private String generatedHtml(Map model, String ftl) {
        try {
            // 创建freemarker配置对象
            Configuration configuration = new Configuration(Configuration.getVersion());
            // 创建加载String字符串模板的freemarker模板加载器
            StringTemplateLoader templateLoader = new StringTemplateLoader();
            // 将模板加入模板加载器
            templateLoader.putTemplate("template", ftl);
            // 将模板加载器加入设置对象中
            configuration.setTemplateLoader(templateLoader);
            // 获得模板对象
            Template template = configuration.getTemplate("template");
            // 进行静态化处理,获得静态化页面并返回
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
            return html;
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 页面静态化执行方法
     * @param pageId 页面id
     * @return 执行结果对象
     */
    @Override
    public ResponseResult post(String pageId){
        // 执行页面静态化
        String pageHtml = this.getPageHtml(pageId);
        // 保存静态化页面到GridFS
        CmsPage cmsPage = this.saveHtml(pageId, pageHtml);
        // 发送消息通知各个服务器进行页面部署
        sendMessagePostPage(cmsPage);

        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 将静态化的生成的页面内容存入GridFs当中
     * @param pageId 页面id
     * @param pageHtml 静态化的页面内容
     * @return 页面对象
     */
    private CmsPage saveHtml(String pageId, String pageHtml){
        // 查询页面对象
        Optional<CmsPage> cmsPageOptional = cmsPageRepository.findById(pageId);
        if (!cmsPageOptional.isPresent()){ // 如果查不出来则代表参数有错误
            ExceptionCast.cast(CmsCode.CMS_PAGEISNULL);
        }
        CmsPage cmsPage = cmsPageOptional.get();
        // 判断该页面的HtmlFileId是否为空,如果为空的话则将其删除
        if (StringUtils.isNotEmpty(cmsPage.getHtmlFileId())){
            gridFsTemplate.delete(Query.query(Criteria.where("_id").is(cmsPage.getHtmlFileId())));
        }
        // 上传新生成的静态页面
        ObjectId objectId = null;
        try {
            // 将String字符串转换为InputStream流并指定字符集,进行上传,                            并指定文件名称
            objectId = gridFsTemplate.store(IOUtils.toInputStream(pageHtml, "UTF-8"), cmsPage.getPageName());
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 重新设置该页面的htmlFileId
        cmsPage.setHtmlFileId(objectId.toString());
        // 保存进数据库
        cmsPageRepository.save(cmsPage);
        // 返回页面对象
        return cmsPage;
    }

    /**
     * 发送消息,消息内容为静态化页面的id
     * @param cmsPage 页面对象
     */
    private void sendMessagePostPage(CmsPage cmsPage){
        // 将静态化页面的id封装进map
        Map<String,String> map = new HashMap<>();
        map.put("pageId",cmsPage.getPageId());
        // 将map转换为JSON格式的字符串
        String jsonString = JSON.toJSONString(map);
        // 发送消息,指定交换机,RoutingKey和消息内容
        rabbitTemplate.convertAndSend(RabbitMQConfig.EX_ROUTING_CMS_POSTPAGE,cmsPage.getSiteId(),jsonString);
    }
}
