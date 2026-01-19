package org.paul.gatewayservice.client;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.paul.grpc.lottery.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LotteryGrpcClient {

    @GrpcClient("lottery_service")
    private LotteryServiceGrpc.LotteryServiceBlockingStub lotteryStub;

    public GetOpenLotteriesResponse getOpenLotteries() {
        var request = GetOpenLotteriesRequest.newBuilder().build();
        return lotteryStub.getOpenLotteries(request);
    }

    public CheckBallotResponse checkBallot(String lotteryType, String endDate, List<Integer> ballotNumbers) {
        var request = CheckBallotRequest.newBuilder()
                .setLotteryType(lotteryType)
                .setEndDate(endDate)
                .addAllBallotNumbers(ballotNumbers)
                .build();
        return lotteryStub.checkBallot(request);
    }
}
