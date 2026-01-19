package org.paul.gatewayservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.paul.gatewayservice.client.BallotGrpcClient;
import org.paul.gatewayservice.controller.domain.ballot.BallotsForParticipantResponse;
import org.paul.gatewayservice.controller.domain.ballot.CreateBallotRequest;
import org.paul.gatewayservice.controller.domain.ballot.CreateBallotResponse;
import org.paul.grpc.ballot.BallotInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ballots")
@RequiredArgsConstructor
public class BallotController {

    private final BallotGrpcClient ballotGrpcClient;

    @PostMapping
    public ResponseEntity<CreateBallotResponse> createBallot(@Valid @RequestBody CreateBallotRequest request) {
        var response = ballotGrpcClient.createBallot(
                request.participantId(),
                request.lotteryId(),
                request.lotteryType(),
                request.lotteryName(),
                request.numbers()
        );
        return ResponseEntity.ok(new CreateBallotResponse(response.getBallotId()));
    }

    @GetMapping("/{participantId}")
    public ResponseEntity<List<BallotsForParticipantResponse>> getBallotsByParticipant(@PathVariable Long participantId) {
        var response = ballotGrpcClient.getBallotsByParticipant(participantId);
        var ballots = response.getBallotsList().stream()
                .map(this::mapBallotInfo)
                .toList();
        return ResponseEntity.ok(ballots);
    }

    private BallotsForParticipantResponse mapBallotInfo(BallotInfo info) {
        return new BallotsForParticipantResponse(
                info.getId(),
                info.getParticipantId(),
                info.getLotteryId(),
                info.getLotteryType(),
                info.getLotteryName(),
                info.getNumbersList(),
                info.getCreatedAt(),
                info.getPrize().isEmpty() ? null : info.getPrize()
        );
    }
}
