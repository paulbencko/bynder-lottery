package org.paul.ballotservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.paul.ballotservice.data.repository.BallotRepository;
import org.paul.common.LotteryType;
import org.paul.common.prizecalculator.PrizeCalculator;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BallotPrizeService {

    private final BallotRepository ballotRepository;
    private static final int BATCH_SIZE = 1000;

    @Transactional
    public void calculateAndUpdatePrizes(Long lotteryId, String lotteryTypeString, int[] winningNumbers) {
        log.info("Calculating prizes for ballots of lottery with id: {}", lotteryId);
        var lotteryType = LotteryType.valueOf(lotteryTypeString);
        var page = 0;
        var hasNext = true;

        while (hasNext) {
            var slice = ballotRepository.findByLotteryId(lotteryId, PageRequest.of(page, BATCH_SIZE));
            var ballots = slice.getContent();
            if (ballots.isEmpty()) {
                break;
            }
            for (var ballot : ballots) {
                var prize = PrizeCalculator.calculate(lotteryType, ballot.getNumbers(), winningNumbers);
                ballot.setPrize(prize);
            }
            ballotRepository.saveAll(ballots);
            page++;
            hasNext = slice.hasNext();
        }
        log.info("Finished calculating prizes for lottery with id: {}", lotteryId);
    }
}
