package org.paul.gatewayservice.controller.domain.lottery;

public record LotteryResponse(
        Long id,
        String name,
        String lotteryType,
        String startDate,
        String endDate
) {}
