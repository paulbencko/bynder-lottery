package org.paul.ballotservice.data.repository;

import org.paul.ballotservice.data.entity.Ballot;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BallotRepository extends JpaRepository<Ballot, Long> {

    List<Ballot> findByParticipantIdOrderByCreatedAtDesc(Long participantId);

    Slice<Ballot> findByLotteryId(Long lotteryId, Pageable pageable);
}
