package org.paul.ballotservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.paul.ballotservice.data.entity.Ballot;
import org.paul.ballotservice.data.repository.BallotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BallotService {

    private final BallotRepository ballotRepository;

    @Transactional
    public Ballot createBallot(Long participantId, Long lotteryId, String lotteryType, String lotteryName, int[] numbers) {
        var ballot = new Ballot();
        ballot.setParticipantId(participantId);
        ballot.setLotteryId(lotteryId);
        ballot.setLotteryType(lotteryType);
        ballot.setLotteryName(lotteryName);
        ballot.setNumbers(numbers);
        ballot.setCreatedAt(LocalDateTime.now());

        var saved = ballotRepository.save(ballot);
        log.info("Created ballot: {} for participant: {} on lottery with id: {} ({})", saved.getId(), participantId, lotteryName, lotteryType);
        return saved;
    }

    public List<Ballot> getBallotsByParticipant(Long participantId) {
        return ballotRepository.findByParticipantIdOrderByCreatedAtDesc(participantId);
    }
}
