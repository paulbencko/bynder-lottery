package org.paul.gatewayservice.controller;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.paul.gatewayservice.client.ParticipantGrpcClient;
import org.paul.gatewayservice.controller.exception.GatewayControllerAdvice;
import org.paul.grpc.participant.RegisterResponse;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ParticipantControllerTest {

    @Mock
    private ParticipantGrpcClient participantGrpcClient;

    @InjectMocks
    private ParticipantController participantController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(participantController)
                .setControllerAdvice(new GatewayControllerAdvice())
                .build();
    }

    @Test
    void registerReturnsParticipantInfo() throws Exception {
        var response = RegisterResponse.newBuilder()
                .setMessage("ok")
                .setParticipantId(1L)
                .build();
        when(participantGrpcClient.register("Test User", "test@example.com", "password"))
                .thenReturn(response);

        mockMvc.perform(post("/api/participants/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Test User",
                                  "email": "test@example.com",
                                  "password": "password"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.participantId").value(1))
                .andExpect(jsonPath("$.name").value("Test User"));
    }

    @Test
    void loginMapsGrpcErrorToHttp() throws Exception {
        when(participantGrpcClient.login("test@example.com", "badpass"))
                .thenThrow(new StatusRuntimeException(Status.UNAUTHENTICATED));

        mockMvc.perform(post("/api/participants/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "test@example.com",
                                  "password": "badpass"
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Unexpected error"));
    }
}
