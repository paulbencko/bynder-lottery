package org.paul.ballotservice.rpc;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.paul.ballotservice.service.BallotService;
import org.paul.grpc.ballot.*;

import java.util.Arrays;

@GrpcService
@RequiredArgsConstructor
public class BallotGrpcService extends BallotServiceGrpc.BallotServiceImplBase {

    private final BallotService ballotService;

    @Override
    public void createBallot(CreateBallotRequest request, StreamObserver<CreateBallotResponse> responseObserver) {
        try {
            var numbers = request.getNumbersList().stream().mapToInt(Integer::intValue).toArray();
            var ballot = ballotService.createBallot(
                    request.getParticipantId(),
                    request.getLotteryId(),
                    request.getLotteryType(),
                    request.getLotteryName(),
                    numbers
            );
            var response = CreateBallotResponse.newBuilder()
                    .setMessage("Ballot created successfully")
                    .setBallotId(ballot.getId())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Failed to create ballot")
                    .asRuntimeException());
        }
    }

    @Override
    public void getBallotsByParticipant(GetBallotsByParticipantRequest request, StreamObserver<GetBallotsByParticipantResponse> responseObserver) {
        var ballots = ballotService.getBallotsByParticipant(request.getParticipantId());
        var responseBuilder = GetBallotsByParticipantResponse.newBuilder();

        for (var ballot : ballots) {
            var infoBuilder = BallotInfo.newBuilder()
                    .setId(ballot.getId())
                    .setParticipantId(ballot.getParticipantId())
                    .setLotteryId(ballot.getLotteryId())
                    .setLotteryType(ballot.getLotteryType())
                    .setLotteryName(ballot.getLotteryName())
                    .addAllNumbers(Arrays.stream(ballot.getNumbers()).boxed().toList())
                    .setCreatedAt(ballot.getCreatedAt().toString());
            if (ballot.getPrize() != null) {
                infoBuilder.setPrize(ballot.getPrize().name());
            }
            responseBuilder.addBallots(infoBuilder.build());
        }
        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }
}
