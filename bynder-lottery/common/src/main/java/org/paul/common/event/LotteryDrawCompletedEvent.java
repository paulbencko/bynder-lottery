package org.paul.common.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LotteryDrawCompletedEvent {

    private Long lotteryId;
    private String lotteryType;
    private int[] winningNumbers;
}
