package org.paul.lotteryservice.data.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.paul.common.LotteryType;

import java.time.LocalDate;

@Entity
@Table(name = "lotteries")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Lottery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "lottery_type", nullable = false)
    private LotteryType lotteryType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LotteryStatus status;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "winning_numbers", columnDefinition = "integer[]")
    private int[] winningNumbers;
}
