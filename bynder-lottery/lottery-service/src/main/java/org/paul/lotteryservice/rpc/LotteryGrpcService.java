package org.paul.lotteryservice.rpc;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.paul.common.LotteryType;
import org.paul.grpc.lottery.*;
import org.paul.lotteryservice.exception.LotteryNotClosedException;
import org.paul.lotteryservice.exception.LotteryNotFoundException;
import org.paul.lotteryservice.service.LotteryService;

import java.time.LocalDate;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class LotteryGrpcService extends LotteryServiceGrpc.LotteryServiceImplBase {

    private final LotteryService lotteryService;

    @Override
    public void getOpenLotteries(GetOpenLotteriesRequest request, StreamObserver<GetOpenLotteriesResponse> responseObserver) {
        var lotteries = lotteryService.getOpenLotteries();
        var response = GetOpenLotteriesResponse.newBuilder();

        for (var lottery : lotteries) {
            var lotteryInfo = LotteryInfo.newBuilder()
                    .setId(lottery.getId())
                    .setName(lottery.getName())
                    .setLotteryType(lottery.getLotteryType().name())
                    .setStartDate(lottery.getStartDate().toString())
                    .setEndDate(lottery.getEndDate().toString())
                    .build();

            response.addLotteries(lotteryInfo);
        }
        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    @Override
    public void checkBallot(CheckBallotRequest request, StreamObserver<CheckBallotResponse> responseObserver) {
        try {
            var type = LotteryType.valueOf(request.getLotteryType());
            var endDate = LocalDate.parse(request.getEndDate());
            var ballotNumbers = request.getBallotNumbersList().stream().mapToInt(Integer::intValue).toArray();

            var prize = lotteryService.checkBallot(type, endDate, ballotNumbers);
            var response = CheckBallotResponse.newBuilder()
                    .setPrize(prize.name())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (LotteryNotFoundException e) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("No lottery found for the selected type and date")
                    .asRuntimeException());
        } catch (LotteryNotClosedException e) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("This lottery draw has not happened yet. Please wait until after the draw.")
                    .asRuntimeException());
        } catch (Exception e) {
            log.error("Error checking ballot", e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("There was a problem checking your bet")
                    .asRuntimeException());
        }
    }
}
