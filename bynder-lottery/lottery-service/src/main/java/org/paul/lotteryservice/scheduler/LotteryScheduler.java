package org.paul.lotteryservice.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.paul.lotteryservice.service.LotteryService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LotteryScheduler {

    private final LotteryService lotteryService;

    @Scheduled(cron = "0 0 6 * * *", zone = "UTC")
    public void createDailyLotteries() {
        log.info("Scheduled job: Creating daily lotteries");
        lotteryService.createDailyLotteries();
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "UTC")
    public void drawWinningCombinations() {
        log.info("Scheduled job: Drawing winning combinations and closing lotteries");
        lotteryService.drawWinningCombinations();
    }
}
