package org.paul.gatewayservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.paul.gatewayservice.client.LotteryGrpcClient;
import org.paul.gatewayservice.controller.domain.lottery.CheckBallotRequest;
import org.paul.gatewayservice.controller.domain.lottery.CheckBallotResponse;
import org.paul.gatewayservice.controller.domain.lottery.LotteryResponse;
import org.paul.grpc.lottery.LotteryInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lotteries")
@RequiredArgsConstructor
public class LotteryController {

    private final LotteryGrpcClient lotteryGrpcClient;

    @GetMapping("/open")
    public ResponseEntity<List<LotteryResponse>> getOpenLotteries() {
        var response = lotteryGrpcClient.getOpenLotteries();
        var lotteries = response.getLotteriesList().stream()
                .map(this::mapLotteryInfo)
                .toList();
        return ResponseEntity.ok(lotteries);
    }

    @PostMapping("/check-ballot")
    public ResponseEntity<CheckBallotResponse> checkBallot(@Valid @RequestBody CheckBallotRequest request) {
        var response = lotteryGrpcClient.checkBallot(
                request.lotteryType(),
                request.endDate(),
                request.ballotNumbers()
        );
        return ResponseEntity.ok(new CheckBallotResponse(response.getPrize()));
    }

    private LotteryResponse mapLotteryInfo(LotteryInfo info) {
        return new LotteryResponse(
                info.getId(),
                info.getName(),
                info.getLotteryType(),
                info.getStartDate(),
                info.getEndDate()
        );
    }
}
