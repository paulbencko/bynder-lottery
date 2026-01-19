package org.paul.lotteryservice.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.paul.common.event.LotteryDrawCompletedEvent;
import org.paul.lotteryservice.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LotteryEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishLotteryDrawCompleted(Long lotteryId, String lotteryType, int[] winningNumbers) {
        var event = new LotteryDrawCompletedEvent(lotteryId, lotteryType, winningNumbers);
        rabbitTemplate.convertAndSend(RabbitMQConfig.LOTTERY_DRAW_COMPLETED_QUEUE, event);
        log.info("Published lottery draw completed event for lottery with id: {} and type: {}", lotteryId, lotteryType);
    }
}
