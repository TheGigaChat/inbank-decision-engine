package com.inbank.decision_engine.dto;

public record LoanConstraintsResponse(
        int minAmount,
        int maxAmount,
        int minPeriod,
        int maxPeriod
) {};
