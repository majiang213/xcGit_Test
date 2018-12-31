package com.xuecheng.mq;

import com.xuecheng.config.RabbitMQConfiguration;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RabbitConsumerEmail {

    /**
     * 注解中指定监听的队列
     * @param text 监听到的消息内容
     */
    @RabbitListener(queues = RabbitMQConfiguration.QUEUE_INFORM_EMAIL)
    public void consumerTest(String text){

        System.out.println(text+"email");
    }
}
