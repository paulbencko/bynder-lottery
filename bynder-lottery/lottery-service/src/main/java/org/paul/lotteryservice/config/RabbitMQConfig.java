package org.paul.lotteryservice.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String LOTTERY_DRAW_COMPLETED_QUEUE = "lottery.draw.completed";

    @Bean
    public Queue lotteryDrawCompletedQueue() {
        return new Queue(LOTTERY_DRAW_COMPLETED_QUEUE, true);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
