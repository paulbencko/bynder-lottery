package org.paul.gatewayservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.paul.gatewayservice.client.ParticipantGrpcClient;
import org.paul.gatewayservice.controller.domain.participant.LoginRequest;
import org.paul.gatewayservice.controller.domain.participant.ParticipantInfoResponse;
import org.paul.gatewayservice.controller.domain.participant.RegisterRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/participants")
@RequiredArgsConstructor
public class ParticipantController {

    private final ParticipantGrpcClient participantGrpcClient;

    @PostMapping("/register")
    public ResponseEntity<ParticipantInfoResponse> register(@Valid @RequestBody RegisterRequest request) {
        var response = participantGrpcClient.register(
                request.name(),
                request.email(),
                request.password()
        );
        return ResponseEntity.ok(new ParticipantInfoResponse(response.getParticipantId(), request.name()));
    }

    @PostMapping("/login")
    public ResponseEntity<ParticipantInfoResponse> login(@Valid @RequestBody LoginRequest request) {
        var response = participantGrpcClient.login(
                request.email(),
                request.password()
        );
        return ResponseEntity.ok(new ParticipantInfoResponse(response.getParticipantId(), response.getName()));
    }
}
