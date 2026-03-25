package com.inbank.decision_engine.controller;

import com.inbank.decision_engine.config.LoanConstraintsProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConfigController.class)
class ConfigControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LoanConstraintsProperties loanConstraintsProperties;

    @Test
    void getConfigReturnsBoundConstraintValues() throws Exception {
        when(loanConstraintsProperties.getMinAmount()).thenReturn(2000);
        when(loanConstraintsProperties.getMaxAmount()).thenReturn(10000);
        when(loanConstraintsProperties.getMinPeriod()).thenReturn(12);
        when(loanConstraintsProperties.getMaxPeriod()).thenReturn(60);

        mockMvc.perform(get("/api/config"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.minAmount").value(2000))
                .andExpect(jsonPath("$.maxAmount").value(10000))
                .andExpect(jsonPath("$.minPeriod").value(12))
                .andExpect(jsonPath("$.maxPeriod").value(60));
    }
}
