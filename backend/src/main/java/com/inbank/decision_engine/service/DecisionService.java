package com.inbank.decision_engine.service;

import com.inbank.decision_engine.dto.DecisionRequest;
import com.inbank.decision_engine.dto.DecisionResponse;

public interface DecisionService {

    DecisionResponse calculateDecision(DecisionRequest request);
}
