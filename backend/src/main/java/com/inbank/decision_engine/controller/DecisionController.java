package com.inbank.decision_engine.controller;

import com.inbank.decision_engine.dto.DecisionRequest;
import com.inbank.decision_engine.dto.DecisionResponse;
import com.inbank.decision_engine.service.DecisionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for handling loan decision requests.
 */
@RestController
@RequestMapping("/api/decision")
public class DecisionController {

    private final DecisionService decisionService;

    public DecisionController(DecisionService decisionService) {
        this.decisionService = decisionService;
    }

    /**
     * Processes loan decision request.
     * Validates input data and delegates decision calculation to the service layer.
     */
    @PostMapping
    public DecisionResponse calculate(@Valid @RequestBody DecisionRequest request) {
        return decisionService.calculateDecision(request);
    }
}
