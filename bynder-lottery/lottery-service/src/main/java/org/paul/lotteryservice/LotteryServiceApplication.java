package org.paul.lotteryservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.paul.lotteryservice.service.LotteryService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@SpringBootApplication
@EnableScheduling
@RequiredArgsConstructor
public class LotteryServiceApplication implements CommandLineRunner {

    private final LotteryService lotteryService;

    public static void main(String[] args) {
        SpringApplication.run(LotteryServiceApplication.class, args);
    }

    @Override
    public void run(String... args) {
        log.info("Initializing lotteries on startup...");
        lotteryService.createDailyLotteries();
    }
}
