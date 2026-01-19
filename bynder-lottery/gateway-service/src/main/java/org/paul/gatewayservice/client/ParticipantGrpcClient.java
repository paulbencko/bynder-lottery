package org.paul.gatewayservice.client;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.paul.grpc.participant.*;
import org.springframework.stereotype.Service;

@Service
public class ParticipantGrpcClient {

    @GrpcClient("participant_service")
    private ParticipantServiceGrpc.ParticipantServiceBlockingStub participantStub;

    public RegisterResponse register(String name, String email, String password) {
        var request = RegisterRequest.newBuilder()
                .setName(name)
                .setEmail(email)
                .setPassword(password)
                .build();
        return participantStub.register(request);
    }

    public LoginResponse login(String email, String password) {
        var request = LoginRequest.newBuilder()
                .setEmail(email)
                .setPassword(password)
                .build();
        return participantStub.login(request);
    }
}
