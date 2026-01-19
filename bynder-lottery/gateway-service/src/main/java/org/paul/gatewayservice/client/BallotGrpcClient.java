package org.paul.gatewayservice.client;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.paul.grpc.ballot.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BallotGrpcClient {

    @GrpcClient("ballot_service")
    private BallotServiceGrpc.BallotServiceBlockingStub ballotStub;

    public CreateBallotResponse createBallot(Long participantId, Long lotteryId, String lotteryType, String lotteryName, List<Integer> numbers) {
        var request = CreateBallotRequest.newBuilder()
                .setParticipantId(participantId)
                .setLotteryId(lotteryId)
                .setLotteryType(lotteryType)
                .setLotteryName(lotteryName)
                .addAllNumbers(numbers)
                .build();
        return ballotStub.createBallot(request);
    }

    public GetBallotsByParticipantResponse getBallotsByParticipant(Long participantId) {
        var request = GetBallotsByParticipantRequest.newBuilder()
                .setParticipantId(participantId)
                .build();
        return ballotStub.getBallotsByParticipant(request);
    }
}
