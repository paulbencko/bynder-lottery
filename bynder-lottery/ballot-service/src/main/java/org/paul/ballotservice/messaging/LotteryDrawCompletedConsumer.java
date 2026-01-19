package org.paul.ballotservice.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.paul.ballotservice.config.RabbitMQConfig;
import org.paul.ballotservice.service.BallotPrizeService;
import org.paul.common.event.LotteryDrawCompletedEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LotteryDrawCompletedConsumer {

    private final BallotPrizeService ballotPrizeService;

    @RabbitListener(queues = RabbitMQConfig.LOTTERY_DRAW_COMPLETED_QUEUE)
    public void handleLotteryDrawCompleted(LotteryDrawCompletedEvent event) {
        log.info("Received lottery draw completed event for lottery with id: {} and type: {}", event.getLotteryId(), event.getLotteryType());
        ballotPrizeService.calculateAndUpdatePrizes(event.getLotteryId(), event.getLotteryType(), event.getWinningNumbers());
    }
}
