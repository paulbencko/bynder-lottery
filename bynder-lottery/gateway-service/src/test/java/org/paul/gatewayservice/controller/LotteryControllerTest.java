package org.paul.gatewayservice.controller;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.paul.gatewayservice.client.LotteryGrpcClient;
import org.paul.gatewayservice.controller.exception.GatewayControllerAdvice;
import org.paul.grpc.lottery.CheckBallotResponse;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class LotteryControllerTest {

    @Mock
    private LotteryGrpcClient lotteryGrpcClient;

    @InjectMocks
    private LotteryController lotteryController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(lotteryController)
                .setControllerAdvice(new GatewayControllerAdvice())
                .build();
    }

    @Test
    void checkBallotReturnsPrize() throws Exception {
        var response = CheckBallotResponse.newBuilder()
                .setPrize("ALL_MATCH")
                .build();
        when(lotteryGrpcClient.checkBallot("EURO_DREAMS", "2025-01-01", List.of(1, 2, 3, 4, 5, 6)))
                .thenReturn(response);

        mockMvc.perform(post("/api/lotteries/check-ballot")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "lotteryType": "EURO_DREAMS",
                                  "endDate": "2025-01-01",
                                  "ballotNumbers": [1, 2, 3, 4, 5, 6]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.prize").value("ALL_MATCH"));
    }

    @Test
    void checkBallotMapsGrpcErrorToHttp() throws Exception {
        when(lotteryGrpcClient.checkBallot("EURO_DREAMS", "2025-01-01", List.of(1, 2, 3, 4, 5, 6)))
                .thenThrow(new StatusRuntimeException(Status.NOT_FOUND));

        mockMvc.perform(post("/api/lotteries/check-ballot")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "lotteryType": "EURO_DREAMS",
                                  "endDate": "2025-01-01",
                                  "ballotNumbers": [1, 2, 3, 4, 5, 6]
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Unexpected error"));
    }

    @Test
    void checkBallotRejectsInvalidType() throws Exception {
        mockMvc.perform(post("/api/lotteries/check-ballot")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "lotteryType": "EURO_DREAMS",
                                  "endDate": "invalid date",
                                  "ballotNumbers": [1, 2, 3, 4, 5, 6]
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid lottery type or date"));
    }
}
