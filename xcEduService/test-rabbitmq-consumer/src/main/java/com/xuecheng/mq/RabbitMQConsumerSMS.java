package com.xuecheng.mq;

import com.xuecheng.config.RabbitMQConfiguration;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQConsumerSMS {

    @RabbitListener(queues = RabbitMQConfiguration.QUEUE_INFORM_SMS)
    public void consumer(String text){
        System.out.println(text+"sms");
    }
}
