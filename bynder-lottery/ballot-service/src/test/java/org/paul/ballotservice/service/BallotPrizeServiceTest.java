package org.paul.ballotservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.paul.ballotservice.data.entity.Ballot;
import org.paul.ballotservice.data.repository.BallotRepository;
import org.paul.common.PrizeType;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BallotPrizeServiceTest {

    @Mock
    private BallotRepository ballotRepository;

    @InjectMocks
    private BallotPrizeService ballotPrizeService;

    @Test
    void calculateAndUpdatePrizesUpdatesBatch() {
        var ballotNoPrize = new Ballot();
        ballotNoPrize.setNumbers(new int[]{1, 2, 3, 7, 8, 9});
        var ballotFirstPrize = new Ballot();
        ballotFirstPrize.setNumbers(new int[]{1, 2, 3, 4, 5, 6});
        var slice = new SliceImpl<>(List.of(ballotNoPrize, ballotFirstPrize));

        when(ballotRepository.findByLotteryId(eq(1L), any(PageRequest.class)))
                .thenReturn(slice)
                .thenReturn(new SliceImpl<>(List.of()));

        ballotPrizeService.calculateAndUpdatePrizes(1L, "EURO_DREAMS", new int[]{1, 2, 3, 4, 5, 6});

        var captor = ArgumentCaptor.forClass(List.class);
        verify(ballotRepository).saveAll(captor.capture());
        var saved = captor.getValue();
        var first = (Ballot) saved.get(0);
        var second = (Ballot) saved.get(1);
        assertEquals(PrizeType.NO_PRIZE, first.getPrize());
        assertEquals(PrizeType.FIRST_PRIZE, second.getPrize());
    }
}
