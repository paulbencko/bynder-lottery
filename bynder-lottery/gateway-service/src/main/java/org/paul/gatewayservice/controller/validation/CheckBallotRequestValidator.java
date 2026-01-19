package org.paul.gatewayservice.controller.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.paul.common.LotteryType;
import org.paul.gatewayservice.controller.domain.lottery.CheckBallotRequest;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class CheckBallotRequestValidator implements ConstraintValidator<ValidCheckBallotRequest, CheckBallotRequest> {
    @Override
    public boolean isValid(CheckBallotRequest value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        try {
            LotteryType.valueOf(value.lotteryType());
            LocalDate.parse(value.endDate());
            return true;
        } catch (IllegalArgumentException | DateTimeParseException e) {
            return false;
        }
    }
}
