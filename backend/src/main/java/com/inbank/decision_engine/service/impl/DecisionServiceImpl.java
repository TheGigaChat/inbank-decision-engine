package com.inbank.decision_engine.service.impl;

import com.inbank.decision_engine.dto.DecisionRequest;
import com.inbank.decision_engine.dto.DecisionResponse;
import com.inbank.decision_engine.service.DecisionService;
import org.springframework.stereotype.Service;

@Service
public class DecisionServiceImpl implements DecisionService {
    @Override
    public DecisionResponse calculateDecision(DecisionRequest request) {
        // TODO: implement business logic
        return null;
    }
}
