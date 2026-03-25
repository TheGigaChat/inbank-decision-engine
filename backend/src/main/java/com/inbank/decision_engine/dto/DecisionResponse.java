package com.inbank.decision_engine.dto;

import com.inbank.decision_engine.model.Decision;

public record DecisionResponse(
    Decision decision,
    Integer approvedAmount,
    Integer approvedPeriod
){};
