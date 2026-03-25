package com.inbank.decision_engine.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DecisionRequest {

    @NotBlank
    private String personalCode;

    @Min(2000)
    @Max(10000)
    private Integer loanAmount;

    @Min(12)
    @Max(60)
    private Integer loanPeriod;
}
