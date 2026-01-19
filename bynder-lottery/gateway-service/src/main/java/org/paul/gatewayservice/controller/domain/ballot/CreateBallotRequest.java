package org.paul.gatewayservice.controller.domain.ballot;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record CreateBallotRequest(
        @NotNull @Positive Long participantId,
        @NotNull @Positive Long lotteryId,
        @NotBlank String lotteryType,
        @NotBlank String lotteryName,
        @NotEmpty List<Integer> numbers
) {}
