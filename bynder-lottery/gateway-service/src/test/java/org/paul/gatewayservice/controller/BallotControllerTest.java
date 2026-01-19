package org.paul.gatewayservice.controller;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.paul.gatewayservice.client.BallotGrpcClient;
import org.paul.gatewayservice.controller.exception.GatewayControllerAdvice;
import org.paul.grpc.ballot.CreateBallotResponse;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BallotControllerTest {

    @Mock
    private BallotGrpcClient ballotGrpcClient;

    @InjectMocks
    private BallotController ballotController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(ballotController)
                .setControllerAdvice(new GatewayControllerAdvice())
                .build();
    }

    @Test
    void createBallotReturnsId() throws Exception {
        var response = CreateBallotResponse.newBuilder()
                .setBallotId(5L)
                .setMessage("ok")
                .build();
        when(ballotGrpcClient.createBallot(1L, 2L, "EURO_DREAMS", "EuroDreams", List.of(1, 2, 3, 4, 5, 6)))
                .thenReturn(response);

        mockMvc.perform(post("/api/ballots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "participantId": 1,
                                  "lotteryId": 2,
                                  "lotteryType": "EURO_DREAMS",
                                  "lotteryName": "EuroDreams",
                                  "numbers": [1, 2, 3, 4, 5, 6]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ballotId").value(5));
    }

    @Test
    void createBallotMapsGrpcErrorToHttp() throws Exception {
        when(ballotGrpcClient.createBallot(1L, 2L, "EURO_DREAMS", "EuroDreams", List.of(1, 2, 3, 4, 5, 6)))
                .thenThrow(new StatusRuntimeException(Status.INTERNAL));

        mockMvc.perform(post("/api/ballots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "participantId": 1,
                                  "lotteryId": 2,
                                  "lotteryType": "EURO_DREAMS",
                                  "lotteryName": "EuroDreams",
                                  "numbers": [1, 2, 3, 4, 5, 6]
                                }
                                """))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.message").value("Unexpected error"));
    }
}
