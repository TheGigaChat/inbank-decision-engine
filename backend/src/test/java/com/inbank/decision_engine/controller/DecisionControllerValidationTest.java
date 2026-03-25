package com.inbank.decision_engine.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inbank.decision_engine.dto.DecisionResponse;
import com.inbank.decision_engine.exception.GlobalExceptionHandler;
import com.inbank.decision_engine.model.Decision;
import com.inbank.decision_engine.service.DecisionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DecisionController.class)
@Import(GlobalExceptionHandler.class)
class DecisionControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DecisionService decisionService;

    @Test
    void calculateReturnsOkForValidRequest() throws Exception {
        when(decisionService.calculateDecision(any())).thenReturn(new DecisionResponse(Decision.POSITIVE, 5000, 50));

        mockMvc.perform(post("/api/decision")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.decision").value("POSITIVE"))
                .andExpect(jsonPath("$.approvedAmount").value(5000))
                .andExpect(jsonPath("$.approvedPeriod").value(50));
    }

    @Test
    void calculateReturnsBadRequestForBlankPersonalCode() throws Exception {
        Map<String, Object> request = validRequest();
        request.put("personalCode", "");

        mockMvc.perform(post("/api/decision")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.personalCode").exists());
    }

    @Test
    void calculateReturnsBadRequestForLoanAmountBelowMinimum() throws Exception {
        Map<String, Object> request = validRequest();
        request.put("loanAmount", 1500);

        mockMvc.perform(post("/api/decision")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.loanAmount").exists());
    }

    @Test
    void calculateReturnsBadRequestForLoanAmountAboveMaximum() throws Exception {
        Map<String, Object> request = validRequest();
        request.put("loanAmount", 11000);

        mockMvc.perform(post("/api/decision")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.loanAmount").exists());
    }

    @Test
    void calculateReturnsBadRequestForLoanPeriodBelowMinimum() throws Exception {
        Map<String, Object> request = validRequest();
        request.put("loanPeriod", 10);

        mockMvc.perform(post("/api/decision")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.loanPeriod").exists());
    }

    @Test
    void calculateReturnsBadRequestForLoanPeriodAboveMaximum() throws Exception {
        Map<String, Object> request = validRequest();
        request.put("loanPeriod", 100);

        mockMvc.perform(post("/api/decision")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.loanPeriod").exists());
    }

    private Map<String, Object> validRequest() {
        return new HashMap<>(Map.of(
                "personalCode", "49002010965",
                "loanAmount", 5000,
                "loanPeriod", 50
        ));
    }
}
