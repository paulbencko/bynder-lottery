package org.paul.lotteryservice.exception;

public class LotteryNotFoundException extends IllegalArgumentException {

    public LotteryNotFoundException(String message) {
        super(message);
    }
}
