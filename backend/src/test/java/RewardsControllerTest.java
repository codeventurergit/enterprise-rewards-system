package com.core.rewards.controller;

import com.core.rewards.dto.CalculationRequestDto;
import com.core.rewards.service.EnterpriseCalculationEngine;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RewardsController.class)
class RewardsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EnterpriseCalculationEngine calculationEngine;

    @Test
    void shouldAcceptValidCalculationCommand() throws Exception {
        CalculationRequestDto validCommand = new CalculationRequestDto("XM-99812", LocalDate.now().minusMonths(3));

        mockMvc.perform(post("/v1/rewards/calculations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validCommand)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.status").value("ACCEPTED"))
                .andExpect(jsonPath("$.traceId").exists());

        verify(calculationEngine, times(1)).updateAndRecalculatePoints(any(CalculationRequestDto.class), anyString());
    }

    @Test
    void shouldRejectWhenCustomerIdIsMissing() throws Exception {
        CalculationRequestDto invalidCommand = new CalculationRequestDto("", LocalDate.now().minusMonths(3));

        mockMvc.perform(post("/v1/rewards/calculations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidCommand)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"));

        verify(calculationEngine, never()).updateAndRecalculatePoints(any(), anyString());
    }

    @Test
    void shouldRejectWhenDateIsInTheFuture() throws Exception {
        CalculationRequestDto invalidCommand = new CalculationRequestDto("XM-99812", LocalDate.now().plusDays(5));

        mockMvc.perform(post("/v1/rewards/calculations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidCommand)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"));

        verify(calculationEngine, never()).updateAndRecalculatePoints(any(), anyString());
    }
}
