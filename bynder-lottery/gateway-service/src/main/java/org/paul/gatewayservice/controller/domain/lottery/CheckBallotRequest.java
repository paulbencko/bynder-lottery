package org.paul.gatewayservice.controller.domain.lottery;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.paul.gatewayservice.controller.validation.ValidCheckBallotRequest;

import java.util.List;

@ValidCheckBallotRequest
public record CheckBallotRequest(
        @NotBlank String lotteryType,
        @NotBlank String endDate,
        @NotEmpty List<Integer> ballotNumbers
) {}
