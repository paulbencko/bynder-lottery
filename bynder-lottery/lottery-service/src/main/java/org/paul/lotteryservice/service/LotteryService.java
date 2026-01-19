package org.paul.lotteryservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.paul.common.LotteryType;
import org.paul.common.PrizeType;
import org.paul.common.prizecalculator.PrizeCalculator;
import org.paul.lotteryservice.data.entity.Lottery;
import org.paul.lotteryservice.data.entity.LotteryStatus;
import org.paul.lotteryservice.data.repository.LotteryRepository;
import org.paul.lotteryservice.exception.LotteryNotClosedException;
import org.paul.lotteryservice.exception.LotteryNotFoundException;
import org.paul.lotteryservice.messaging.LotteryEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class LotteryService {

    private final LotteryRepository lotteryRepository;
    private final LotteryEventPublisher eventPublisher;

    public List<Lottery> getOpenLotteries() {
        return lotteryRepository.findByStatus(LotteryStatus.OPEN);
    }

    public PrizeType checkBallot(LotteryType type, LocalDate endDate, int[] ballotNumbers) {
        var lottery = lotteryRepository.findByLotteryTypeAndEndDate(type, endDate)
                .orElseThrow(() -> new LotteryNotFoundException("Lottery not found"));

        if (lottery.getStatus() != LotteryStatus.CLOSED) {
            throw new LotteryNotClosedException("Lottery is not closed yet");
        }

        int[] winningNumbers = lottery.getWinningNumbers();
        return PrizeCalculator.calculate(type, ballotNumbers, winningNumbers);
    }

    @Transactional
    public void createDailyLotteries() {
        var today = LocalDate.now();
        log.info("Creating daily lotteries for: {}", today);
        for (LotteryType type : LotteryType.values()) {
            createLotteryIfNoOpenExists(type, today);
        }
    }

    private void createLotteryIfNoOpenExists(LotteryType type, LocalDate startDate) {
        if (lotteryRepository.existsByLotteryTypeAndStatus(type, LotteryStatus.OPEN)) {
            log.info("Open lottery already exists for type: {}, skipping creation", type);
            return;
        }
        var lottery = new Lottery();
        lottery.setName(type.getDisplayName());
        lottery.setLotteryType(type);
        lottery.setStatus(LotteryStatus.OPEN);
        lottery.setStartDate(startDate);
        lottery.setEndDate(startDate);
        lotteryRepository.save(lottery);
        log.info("Created lottery: {} for: {}", type.getDisplayName(), startDate);
    }

    @Transactional
    public void drawWinningCombinations() {
        var today = LocalDate.now();
        log.info("Drawing winning combinations for lotteries ending today: {}", today);

        var lotteriesEndingToday = lotteryRepository.findByStatusAndEndDate(LotteryStatus.OPEN, today);

        for (Lottery lottery : lotteriesEndingToday) {
            int[] winningNumbers = generateWinningNumbers(lottery.getLotteryType());
            lottery.setWinningNumbers(winningNumbers);
            lottery.setStatus(LotteryStatus.CLOSED);
            lotteryRepository.save(lottery);
            log.info("Drew winning numbers for {}: {}", lottery.getLotteryType(), Arrays.toString(winningNumbers));
            eventPublisher.publishLotteryDrawCompleted(lottery.getId(), lottery.getLotteryType().name(), winningNumbers);
        }
    }

    private int[] generateWinningNumbers(LotteryType type) {
        return switch (type) {
            case EURO_MILLONES -> generateNumbers(5, 50);
            case EL_GORDO -> generateNumbers(5, 54);
            case EURO_DREAMS -> generateNumbers(6, 40);
        };
    }

    private int[] generateNumbers(int count, int max) {
        return new Random().ints(1, max + 1)
                .distinct()
                .limit(count)
                .sorted()
                .toArray();
    }
}
