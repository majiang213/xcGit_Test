package com.xuecheng.manage_cms.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CmsRabbitMQConfiguration {

    //队列bean的名称
    public static final String QUEUE_CMS_POSTPAGE = "queue_cms_postpage";
    //交换机的名称
    public static final String EX_ROUTING_CMS_POSTPAGE = "ex_routing_cms_postpage";

    // 队列的名称,每台服务器对应一个队列
    @Value("${xuecheng.mq.queue}")
    private String queueName;

    // routingKey,每个站点的站点ID为routingKey
    @Value("${xuecheng.mq.routingKey}")
    private String routingKey;

    /**
     * 声明队列,
     * 队列名为配置文件中的队列名
     * 队列bean在spring容器中的名称为类中的常亮
     * @return 返回队列Queue
     */
    @Bean(QUEUE_CMS_POSTPAGE)
    public Queue queue_cms_postpage() {
        return new Queue(queueName);
    }

    /**
     * 声明交换机,交换机名称为类中的常量和在IOC容器中的名称一致
     * @return 返回值为交换机名称
     */
    @Bean(EX_ROUTING_CMS_POSTPAGE)
    public Exchange EX_ROUTING_CMS_POSTPAGE(){
        return ExchangeBuilder.directExchange(EX_ROUTING_CMS_POSTPAGE).durable(true).build();
    }

    /**
     * 将队列和交换机进行绑定
     * @param queue_cms_postpage 上面代码声明的队列
     * @param ex_routing_cms_postpage 上面代码声明的交换机
     * @return 返回绑定对象
     */
    @Bean
    public Binding BINDING_QUEUE_INFORM_CMS(@Qualifier(QUEUE_CMS_POSTPAGE) Queue queue_cms_postpage,
                           @Qualifier(EX_ROUTING_CMS_POSTPAGE) Exchange ex_routing_cms_postpage){
        return BindingBuilder.bind(queue_cms_postpage).to(ex_routing_cms_postpage).with(routingKey).noargs();
    }
}
