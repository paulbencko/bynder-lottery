package org.paul.common.prizecalculator;

import org.paul.common.LotteryType;
import org.paul.common.PrizeType;

import java.util.Arrays;

public final class PrizeCalculator {

    private PrizeCalculator() {
    }

    public static PrizeType calculate(LotteryType type, int[] ballotNumbers, int[] winningNumbers) {
        int totalNumbers = type.getPlayableNumbers();
        int matchingNumbers = countMatches(ballotNumbers, winningNumbers);

        return switch (totalNumbers - matchingNumbers) {
            case 0 -> PrizeType.FIRST_PRIZE;
            case 1 -> PrizeType.SECOND_PRIZE;
            case 2 -> PrizeType.THIRD_PRIZE;
            default -> PrizeType.NO_PRIZE;
        };
    }

    private static int countMatches(int[] ballotNumbers, int[] winningNumbers) {
        return (int) Arrays.stream(ballotNumbers)
                .filter(ballotNumber ->
                        Arrays.stream(winningNumbers)
                                .anyMatch(winningNumber -> winningNumber == ballotNumber))
                .count();
    }
}
