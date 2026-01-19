package org.paul.lotteryservice.data.repository;

import org.paul.common.LotteryType;
import org.paul.lotteryservice.data.entity.Lottery;
import org.paul.lotteryservice.data.entity.LotteryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LotteryRepository extends JpaRepository<Lottery, Long> {

    List<Lottery> findByStatus(LotteryStatus status);

    List<Lottery> findByStatusAndEndDate(LotteryStatus status, LocalDate endDate);

    Optional<Lottery> findByLotteryTypeAndEndDate(LotteryType type, LocalDate endDate);

    boolean existsByLotteryTypeAndStatus(LotteryType type, LotteryStatus status);
}
