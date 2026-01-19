package org.paul.ballotservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.paul.ballotservice.data.entity.Ballot;
import org.paul.ballotservice.data.repository.BallotRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BallotServiceTest {

    @Mock
    private BallotRepository ballotRepository;

    @InjectMocks
    private BallotService ballotService;

    @Test
    void createBallotSavesWithCreatedAt() {
        var saved = new Ballot();
        saved.setId(10L);
        when(ballotRepository.save(any(Ballot.class))).thenReturn(saved);

        var result = ballotService.createBallot(1L, 2L, "EURO_DREAMS", "Euro Dreams", new int[]{1, 2, 3, 4, 5, 6});

        assertEquals(10L, result.getId());
        var captor = ArgumentCaptor.forClass(Ballot.class);
        verify(ballotRepository).save(captor.capture());
        var created = captor.getValue();
        assertEquals(1L, created.getParticipantId());
        assertEquals(2L, created.getLotteryId());
        assertEquals("EURO_DREAMS", created.getLotteryType());
        assertEquals("Euro Dreams", created.getLotteryName());
        assertNotNull(created.getCreatedAt());
    }
}
