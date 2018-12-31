package com.xuecheng.manage_cms.mq;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import com.xuecheng.manage_cms.service.PageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
public class ConsumerPostPage {

    @Autowired
    private PageService pageService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerPostPage.class);

    @Autowired
    private CmsPageRepository cmsPageRepository;

    /**
     * 监听该工程对应的队列
     * @param jsonString 获取队列中的消息,然后页面发布方法
     */
    @RabbitListener(queues = "${xuecheng.mq.queue}")
    public void post(String jsonString){
        // 将json格式字符串转换为Map集合
        Map map = JSON.parseObject(jsonString, Map.class);
        // 记录日志
        LOGGER.info("receive cms post page:{}",jsonString);
        // 通过pageId进行查询
        Optional<CmsPage> optional = cmsPageRepository.findById((String) map.get("pageId"));
        if (optional.isPresent()){
            pageService.postPage(optional.get().getPageId());
            return;
        }
        // 查询结果为null的话则记录错误日志
        LOGGER.error("receive cms post page, pageId is null:{}",jsonString);
    }
}
