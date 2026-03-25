package com.inbank.decision_engine.model;

public record PersonProfile(
        Integer creditModifier,
        boolean hasDebt
) {};
