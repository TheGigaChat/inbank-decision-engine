package com.inbank.decision_engine.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DecisionRequest {

    @NotBlank(message = "personalCode must not be blank")
    private String personalCode;

    @Min(value = 2000, message = "loanAmount must be at least 2000")
    @Max(value = 10000, message = "loanAmount must not exceed 10000")
    private Integer loanAmount;

    @Min(value = 12, message = "loanPeriod must be at least 12")
    @Max(value = 60, message = "loanPeriod must not exceed 60")
    private Integer loanPeriod;
}
