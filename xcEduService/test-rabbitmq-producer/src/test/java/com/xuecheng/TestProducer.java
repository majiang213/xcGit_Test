package com.xuecheng;

import com.xuecheng.config.RabbitMQConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void sendMessage(){
        String mail = "给你发邮件了,快看";
        // 调用发送消息方法,指定交换机,routingKey,消息内容
        rabbitTemplate.convertAndSend(RabbitMQConfiguration.EXCHANGE_TOPICS_INFORM,"inform.email",mail);

        String sms = "给你发短信了,快去看";
        rabbitTemplate.convertAndSend(RabbitMQConfiguration.EXCHANGE_TOPICS_INFORM,"inform.sms",sms);

        String smsAndMail = "短信和邮件都给你发了,快去看";
        rabbitTemplate.convertAndSend(RabbitMQConfiguration.EXCHANGE_TOPICS_INFORM,"inform.email.sms",smsAndMail);
    }
}
