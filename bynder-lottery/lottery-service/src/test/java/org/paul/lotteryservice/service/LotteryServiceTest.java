package org.paul.lotteryservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.paul.common.LotteryType;
import org.paul.common.PrizeType;
import org.paul.lotteryservice.data.entity.Lottery;
import org.paul.lotteryservice.data.entity.LotteryStatus;
import org.paul.lotteryservice.data.repository.LotteryRepository;
import org.paul.lotteryservice.exception.LotteryNotClosedException;
import org.paul.lotteryservice.exception.LotteryNotFoundException;
import org.paul.lotteryservice.messaging.LotteryEventPublisher;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LotteryServiceTest {

    @Mock
    private LotteryRepository lotteryRepository;

    @Mock
    private LotteryEventPublisher eventPublisher;

    @InjectMocks
    private LotteryService lotteryService;

    @Test
    void checkBallotReturnsPrizeWhenClosed() {
        var lottery = new Lottery();
        lottery.setStatus(LotteryStatus.CLOSED);
        lottery.setWinningNumbers(new int[]{1, 2, 3, 4, 5, 6});
        when(lotteryRepository.findByLotteryTypeAndEndDate(LotteryType.EURO_DREAMS, LocalDate.of(2025, 1, 1)))
                .thenReturn(Optional.of(lottery));

        var prize = lotteryService.checkBallot(LotteryType.EURO_DREAMS, LocalDate.of(2025, 1, 1), new int[]{1, 2, 3, 4, 5, 6});

        assertEquals(PrizeType.FIRST_PRIZE, prize);
    }

    @Test
    void checkBallotThrowsWhenLotteryMissing() {
        when(lotteryRepository.findByLotteryTypeAndEndDate(LotteryType.EURO_DREAMS, LocalDate.of(2025, 1, 1)))
                .thenReturn(Optional.empty());

        assertThrows(LotteryNotFoundException.class, () ->
                lotteryService.checkBallot(LotteryType.EURO_DREAMS, LocalDate.of(2025, 1, 1), new int[]{1, 2, 3, 4, 5, 6}));
    }

    @Test
    void checkBallotThrowsWhenLotteryOpen() {
        var lottery = new Lottery();
        lottery.setStatus(LotteryStatus.OPEN);
        when(lotteryRepository.findByLotteryTypeAndEndDate(LotteryType.EURO_DREAMS, LocalDate.of(2025, 1, 1)))
                .thenReturn(Optional.of(lottery));

        assertThrows(LotteryNotClosedException.class, () ->
                lotteryService.checkBallot(LotteryType.EURO_DREAMS, LocalDate.of(2025, 1, 1), new int[]{1, 2, 3, 4, 5, 6}));
    }

    @Test
    void createDailyLotteriesCreatesForEachTypeWhenOpenMissing() {
        when(lotteryRepository.existsByLotteryTypeAndStatus(any(), eq(LotteryStatus.OPEN))).thenReturn(false);

        lotteryService.createDailyLotteries();

        verify(lotteryRepository, times(LotteryType.values().length)).save(any(Lottery.class));
    }

    @Test
    void createDailyLotteriesSkipsWhenOpenExists() {
        when(lotteryRepository.existsByLotteryTypeAndStatus(any(), eq(LotteryStatus.OPEN))).thenReturn(true);

        lotteryService.createDailyLotteries();

        verify(lotteryRepository, never()).save(any(Lottery.class));
    }

    @Test
    void drawWinningCombinationsClosesAndPublishes() {
        var lottery = new Lottery();
        lottery.setId(10L);
        lottery.setLotteryType(LotteryType.EURO_DREAMS);
        lottery.setStatus(LotteryStatus.OPEN);
        lottery.setEndDate(LocalDate.now());
        when(lotteryRepository.findByStatusAndEndDate(LotteryStatus.OPEN, LocalDate.now()))
                .thenReturn(List.of(lottery));

        lotteryService.drawWinningCombinations();

        verify(lotteryRepository).save(lottery);
        verify(eventPublisher).publishLotteryDrawCompleted(eq(10L), eq(LotteryType.EURO_DREAMS.name()), any(int[].class));
        assertEquals(LotteryStatus.CLOSED, lottery.getStatus());
    }
}
