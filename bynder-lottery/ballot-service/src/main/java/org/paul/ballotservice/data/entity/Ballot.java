package org.paul.ballotservice.data.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.paul.common.PrizeType;

import java.time.LocalDateTime;

@Entity
@Table(name = "ballots")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ballot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "participant_id", nullable = false)
    private Long participantId;

    @Column(name = "lottery_id", nullable = false)
    private Long lotteryId;

    @Column(name = "lottery_type", nullable = false)
    private String lotteryType;

    @Column(name = "lottery_name", nullable = false)
    private String lotteryName;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "numbers", columnDefinition = "integer[]", nullable = false)
    private int[] numbers;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "prize")
    private PrizeType prize;
}
