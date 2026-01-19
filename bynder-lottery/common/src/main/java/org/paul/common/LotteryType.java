package org.paul.common;

import lombok.Getter;

@Getter
public enum LotteryType {

    EURO_MILLONES(5, "Euro Millones"),
    EL_GORDO(5, "El Gordo"),
    EURO_DREAMS(6, "Euro Dreams");

    private final int playableNumbers;
    private final String displayName;

    LotteryType(int playableNumbers, String displayName) {
        this.playableNumbers = playableNumbers;
        this.displayName = displayName;
    }
}
