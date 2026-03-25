package com.inbank.decision_engine.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "loan.constraints")
public class LoanConstraintsProperties {
    private int minAmount;
    private int maxAmount;
    private int minPeriod;
    private int maxPeriod;
}
