package org.paul.lotteryservice.exception;

public class LotteryNotClosedException extends IllegalStateException {

    public LotteryNotClosedException(String message) {
        super(message);
    }
}
