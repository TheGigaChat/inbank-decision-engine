package com.inbank.decision_engine;

import com.inbank.decision_engine.config.LoanConstraintsProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class LoanConstraintsPropertiesBindingTest {

    @Autowired
    private LoanConstraintsProperties loanConstraintsProperties;

    @Test
    void bindsLoanConstraintsFromApplicationYaml() {
        assertEquals(2000, loanConstraintsProperties.getMinAmount());
        assertEquals(10000, loanConstraintsProperties.getMaxAmount());
        assertEquals(12, loanConstraintsProperties.getMinPeriod());
        assertEquals(60, loanConstraintsProperties.getMaxPeriod());
    }
}
