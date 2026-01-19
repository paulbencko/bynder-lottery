package org.paul.participantservice.rpc;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.paul.grpc.participant.*;
import org.paul.participantservice.exception.EmailAlreadyRegisteredException;
import org.paul.participantservice.service.ParticipantService;

@GrpcService
@RequiredArgsConstructor
public class ParticipantGrpcService extends ParticipantServiceGrpc.ParticipantServiceImplBase {

    private final ParticipantService participantService;

    @Override
    public void register(RegisterRequest request, StreamObserver<RegisterResponse> responseObserver) {
        try {
            var participant = participantService.register(
                    request.getName(),
                    request.getEmail(),
                    request.getPassword()
            );
            var response = RegisterResponse.newBuilder()
                    .setMessage("Registration successful")
                    .setParticipantId(participant.getId())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (EmailAlreadyRegisteredException e) {
            responseObserver.onError(
                    Status.ALREADY_EXISTS.withDescription(e.getMessage()).asRuntimeException());
        } catch (Exception e) {
            responseObserver.onError(
                    Status.INTERNAL
                    .withDescription("Failed to register participant")
                    .asRuntimeException());
        }
    }

    @Override
    public void login(LoginRequest request, StreamObserver<LoginResponse> responseObserver) {
        var participant = participantService.login(
                request.getEmail(),
                request.getPassword()
        );
        if (participant.isEmpty()) {
            responseObserver.onError(Status.UNAUTHENTICATED
                    .withDescription("Invalid email or password")
                    .asRuntimeException());
            return;
        }
        var value = participant.get();
        var response = LoginResponse.newBuilder()
                .setMessage("Login successful")
                .setParticipantId(value.getId())
                .setName(value.getName())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
