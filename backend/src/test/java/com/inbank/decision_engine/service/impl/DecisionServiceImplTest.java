package com.inbank.decision_engine.service.impl;

import com.inbank.decision_engine.config.LoanConstraintsProperties;
import com.inbank.decision_engine.dto.DecisionRequest;
import com.inbank.decision_engine.dto.DecisionResponse;
import com.inbank.decision_engine.model.PersonProfileEntity;
import com.inbank.decision_engine.model.Decision;
import com.inbank.decision_engine.repository.PersonProfileRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DecisionServiceImplTest {

    @Test
    void calculateDecisionReturnsNegativeDecisionWhenProfileHasDebt() {
        DecisionServiceImpl decisionService = serviceWithProfile("49002010998", 0, true);

        DecisionResponse response = decisionService.calculateDecision(buildRequest("49002010998"));

        assertEquals(Decision.NEGATIVE, response.decision());
        assertNull(response.approvedAmount());
        assertNull(response.approvedPeriod());
    }

    @Test
    void calculateDecisionReturnsPositiveDecisionWhenSelectedPeriodSupportsRequestedAmount() {
        DecisionServiceImpl decisionService = serviceWithProfile("49002010965", 100, false);

        DecisionResponse response = decisionService.calculateDecision(buildRequest("49002010965", 5000, 50));

        assertEquals(Decision.POSITIVE, response.decision());
        assertEquals(5000, response.approvedAmount());
        assertEquals(50, response.approvedPeriod());
    }

    @Test
    void calculateDecisionReturnsMaximumAmountForSelectedPeriodWhenItExceedsRequestedAmount() {
        DecisionServiceImpl decisionService = serviceWithProfile("49002010965", 100, false);

        DecisionResponse response = decisionService.calculateDecision(buildRequest("49002010965", 2000, 50));

        assertEquals(Decision.POSITIVE, response.decision());
        assertEquals(5000, response.approvedAmount());
        assertEquals(50, response.approvedPeriod());
    }

    @Test
    void calculateDecisionReturnsMinimumValidOfferAtSmallestEligiblePeriodWhenSelectedPeriodIsInvalid() {
        DecisionServiceImpl decisionService = serviceWithProfile("49002010965", 100, false);

        DecisionResponse response = decisionService.calculateDecision(buildRequest("49002010965", 2000, 12));

        assertEquals(Decision.POSITIVE, response.decision());
        assertEquals(2000, response.approvedAmount());
        assertEquals(20, response.approvedPeriod());
    }

    @Test
    void calculateDecisionReturnsMinimumValidOfferWhenRequestedAmountCannotBeReached() {
        DecisionServiceImpl decisionService = serviceWithProfile("49002010965", 100, false);

        DecisionResponse response = decisionService.calculateDecision(buildRequest("49002010965", 10000, 12));

        assertEquals(Decision.POSITIVE, response.decision());
        assertEquals(2000, response.approvedAmount());
        assertEquals(20, response.approvedPeriod());
    }

    @Test
    void calculateDecisionReturnsSelectedPeriodAmountWhenItIsValidButLowerThanRequestedAmount() {
        DecisionServiceImpl decisionService = serviceWithProfile("49002010965", 100, false);

        DecisionResponse response = decisionService.calculateDecision(buildRequest("49002010965", 3000, 23));

        assertEquals(Decision.POSITIVE, response.decision());
        assertEquals(2300, response.approvedAmount());
        assertEquals(23, response.approvedPeriod());
    }

    @Test
    void calculateDecisionReturnsNegativeWhenNoValidOfferExistsEvenAtMaximumPeriod() {
        DecisionServiceImpl decisionService = serviceWithProfile("49002010965", 10, false);

        DecisionResponse response = decisionService.calculateDecision(buildRequest("49002010965", 2000, 12));

        assertEquals(Decision.NEGATIVE, response.decision());
        assertNull(response.approvedAmount());
        assertNull(response.approvedPeriod());
    }

    @Test
    void calculateDecisionReturnsNegativeDecisionForUnknownPersonalCodeWithZeroModifier() {
        DecisionServiceImpl decisionService = serviceWithUnknownProfile();

        DecisionResponse response = decisionService.calculateDecision(buildRequest("00000000000", 2000, 12));

        assertEquals(Decision.NEGATIVE, response.decision());
        assertNull(response.approvedAmount());
        assertNull(response.approvedPeriod());
    }

    @Test
    void calculateDecisionAppliesMaximumCapAndOptimizesPeriod() {
        DecisionServiceImpl decisionService = serviceWithProfile("49002010987", 1000, false);

        DecisionResponse response = decisionService.calculateDecision(buildRequest("49002010987", 2000, 20));

        assertEquals(Decision.POSITIVE, response.decision());
        assertEquals(10000, response.approvedAmount());
        assertEquals(12, response.approvedPeriod());
    }

    // ---------- helpers ----------

    private DecisionServiceImpl serviceWithProfile(String personalCode, int creditModifier, boolean hasDebt) {
        PersonProfileRepository repo = mock(PersonProfileRepository.class);

        PersonProfileEntity entity = new PersonProfileEntity();
        entity.setPersonalCode(personalCode);
        entity.setCreditModifier(creditModifier);
        entity.setHasDebt(hasDebt);

        when(repo.findById(personalCode)).thenReturn(Optional.of(entity));

        return new DecisionServiceImpl(repo, defaultConstraints());
    }

    private DecisionServiceImpl serviceWithUnknownProfile() {
        PersonProfileRepository repo = mock(PersonProfileRepository.class);
        when(repo.findById(anyString())).thenReturn(Optional.empty());

        return new DecisionServiceImpl(repo, defaultConstraints());
    }

    private LoanConstraintsProperties defaultConstraints() {
        LoanConstraintsProperties constraints = new LoanConstraintsProperties();
        constraints.setMinAmount(2000);
        constraints.setMaxAmount(10000);
        constraints.setMinPeriod(12);
        constraints.setMaxPeriod(60);
        return constraints;
    }

    private DecisionRequest buildRequest(String personalCode) {
        return buildRequest(personalCode, 2000, 12);
    }

    private DecisionRequest buildRequest(String personalCode, int loanAmount, int loanPeriod) {
        DecisionRequest request = new DecisionRequest();
        request.setPersonalCode(personalCode);
        request.setLoanAmount(loanAmount);
        request.setLoanPeriod(loanPeriod);
        return request;
    }
}
