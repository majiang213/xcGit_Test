package com.xuecheng.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfiguration {

    public static final String QUEUE_INFORM_EMAIL = "queue_inform_email";
    public static final String QUEUE_INFORM_SMS = "queue_inform_sms";
    public static final String EXCHANGE_TOPICS_INFORM="exchange_topics_inform";
    public static final String ROUTINGKEY_EMAIL="inform.#.email.#";
    public static final String ROUTINGKEY_SMS="inform.#.sms.#";

    @Bean(EXCHANGE_TOPICS_INFORM)
    public Exchange EXCHANGE_TOPICS_INFORM(){
        return ExchangeBuilder.topicExchange(EXCHANGE_TOPICS_INFORM).durable(true).build();
    }

    @Bean(QUEUE_INFORM_EMAIL)
    public Queue QUEUE_INFORM_EMAIL(){
        return new Queue(QUEUE_INFORM_EMAIL);
    }

    @Bean(QUEUE_INFORM_SMS)
    public Queue QUEUE_INFORM_SMS(){
        return new Queue(QUEUE_INFORM_SMS);
    }

    @Bean
    public Binding BINDING_QUEUE_INFORM_SMS(@Qualifier("queue_inform_sms") Queue QUEUE_INFORM_SMS,
                                              @Qualifier("exchange_topics_inform") Exchange EXCHANGE_TOPICS_INFORM){
        return BindingBuilder.bind(QUEUE_INFORM_SMS).to(EXCHANGE_TOPICS_INFORM).with(ROUTINGKEY_SMS).noargs();
    }

    @Bean
    public Binding BINDING_QUEUE_INFORM_EMAIL(@Qualifier("queue_inform_email") Queue QUEUE_INFORM_EMAIL,
                                              @Qualifier("exchange_topics_inform") Exchange EXCHANGE_TOPICS_INFORM){
        return BindingBuilder.bind(QUEUE_INFORM_EMAIL).to(EXCHANGE_TOPICS_INFORM).with(ROUTINGKEY_EMAIL).noargs();
    }


}
