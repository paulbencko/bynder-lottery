package org.paul.common.prizecalculator;

import org.junit.jupiter.api.Test;
import org.paul.common.LotteryType;
import org.paul.common.PrizeType;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PrizeCalculatorTest {

    @Test
    void calculatesCorrectPrizeForMatchCounts() {
        var winning = new int[]{1, 2, 3, 4, 5, 6};

        assertEquals(PrizeType.FIRST_PRIZE,
                PrizeCalculator.calculate(LotteryType.EURO_DREAMS, new int[]{1, 2, 3, 4, 5, 6}, winning));
        assertEquals(PrizeType.SECOND_PRIZE,
                PrizeCalculator.calculate(LotteryType.EURO_DREAMS, new int[]{1, 2, 3, 4, 5, 7}, winning));
        assertEquals(PrizeType.THIRD_PRIZE,
                PrizeCalculator.calculate(LotteryType.EURO_DREAMS, new int[]{1, 2, 3, 4, 7, 8}, winning));
        assertEquals(PrizeType.NO_PRIZE,
                PrizeCalculator.calculate(LotteryType.EURO_DREAMS, new int[]{1, 2, 3, 7, 8, 9}, winning));
    }
}
