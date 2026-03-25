package com.inbank.decision_engine.controller;

import com.inbank.decision_engine.config.LoanConstraintsProperties;
import com.inbank.decision_engine.dto.LoanConstraintsResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConfigController {

    private final LoanConstraintsProperties constraints;

    public ConfigController(LoanConstraintsProperties loanConstraintsProperties) {
        this.constraints = loanConstraintsProperties;
    }

    /**
     * Exposes loan constraint configuration for frontend consumption.
     */
    @GetMapping("/api/config")
    public LoanConstraintsResponse getConfig(){
        return new LoanConstraintsResponse(
                constraints.getMinAmount(),
                constraints.getMaxAmount(),
                constraints.getMinPeriod(),
                constraints.getMaxPeriod()
        );
    }
}
