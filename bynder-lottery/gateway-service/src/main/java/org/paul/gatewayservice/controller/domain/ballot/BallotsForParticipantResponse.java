package org.paul.gatewayservice.controller.domain.ballot;

import java.util.List;

public record BallotsForParticipantResponse(
        Long id,
        Long participantId,
        Long lotteryId,
        String lotteryType,
        String lotteryName,
        List<Integer> numbers,
        String createdAt,
        String prize
) {}
