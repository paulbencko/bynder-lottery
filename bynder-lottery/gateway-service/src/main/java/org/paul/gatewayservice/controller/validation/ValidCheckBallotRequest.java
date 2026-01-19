package org.paul.gatewayservice.controller.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CheckBallotRequestValidator.class)
@Documented
public @interface ValidCheckBallotRequest {
    String message() default "Invalid lottery type or date";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
