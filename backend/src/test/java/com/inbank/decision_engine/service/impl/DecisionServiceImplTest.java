package com.inbank.decision_engine.service.impl;

import com.inbank.decision_engine.config.LoanConstraintsProperties;
import com.inbank.decision_engine.dto.DecisionRequest;
import com.inbank.decision_engine.dto.DecisionResponse;
import com.inbank.decision_engine.model.Decision;
import com.inbank.decision_engine.model.PersonProfile;
import com.inbank.decision_engine.service.ProfileService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class DecisionServiceImplTest {

    @Test
    void calculateDecisionReturnsNegativeDecisionWhenProfileHasDebt() {
        ProfileService profileService = personalCode -> new PersonProfile(0, true);
        DecisionServiceImpl decisionService = new DecisionServiceImpl(profileService, defaultConstraints());

        DecisionResponse response = decisionService.calculateDecision(buildRequest("49002010998"));

        assertEquals(Decision.NEGATIVE, response.decision());
        assertNull(response.approvedAmount());
        assertNull(response.approvedPeriod());
    }

    @Test
    void calculateDecisionReturnsPositiveDecisionWhenSelectedPeriodSupportsRequestedAmount() {
        DecisionServiceImpl decisionService = new DecisionServiceImpl(
                personalCode -> new PersonProfile(100, false),
                defaultConstraints()
        );

        DecisionResponse response = decisionService.calculateDecision(buildRequest("49002010965", 5000, 50));

        assertEquals(Decision.POSITIVE, response.decision());
        assertEquals(5000, response.approvedAmount());
        assertEquals(50, response.approvedPeriod());
    }

    @Test
    void calculateDecisionReturnsMaximumAmountForSelectedPeriodWhenItExceedsRequestedAmount() {
        DecisionServiceImpl decisionService = new DecisionServiceImpl(
                personalCode -> new PersonProfile(100, false),
                defaultConstraints()
        );

        DecisionResponse response = decisionService.calculateDecision(buildRequest("49002010965", 2000, 50));

        assertEquals(Decision.POSITIVE, response.decision());
        assertEquals(5000, response.approvedAmount());
        assertEquals(50, response.approvedPeriod());
    }

    @Test
    void calculateDecisionReturnsRequestedAmountAtSmallestAlternativePeriodThatSatisfiesRequest() {
        DecisionServiceImpl decisionService = new DecisionServiceImpl(
                personalCode -> new PersonProfile(100, false),
                defaultConstraints()
        );

        DecisionResponse response = decisionService.calculateDecision(buildRequest("49002010965", 2000, 12));

        assertEquals(Decision.POSITIVE, response.decision());
        assertEquals(2000, response.approvedAmount());
        assertEquals(20, response.approvedPeriod());
    }

    @Test
    void calculateDecisionReturnsLaterAlternativePeriodWhenEarlierPeriodsCannotSatisfyRequest() {
        DecisionServiceImpl decisionService = new DecisionServiceImpl(
                personalCode -> new PersonProfile(100, false),
                defaultConstraints()
        );

        DecisionResponse response = decisionService.calculateDecision(buildRequest("49002010965", 5000, 12));

        assertEquals(Decision.POSITIVE, response.decision());
        assertEquals(5000, response.approvedAmount());
        assertEquals(50, response.approvedPeriod());
    }

    @Test
    void calculateDecisionReturnsLargestPossibleOfferWhenNoPeriodSatisfiesRequestedAmount() {
        DecisionServiceImpl decisionService = new DecisionServiceImpl(
                personalCode -> new PersonProfile(100, false),
                defaultConstraints()
        );

        DecisionResponse response = decisionService.calculateDecision(buildRequest("49002010965", 10000, 12));

        assertEquals(Decision.POSITIVE, response.decision());
        assertEquals(6000, response.approvedAmount());
        assertEquals(60, response.approvedPeriod());
    }

    @Test
    void calculateDecisionReturnsNegativeDecisionForUnknownPersonalCodeWithZeroModifier() {
        DecisionServiceImpl decisionService = new DecisionServiceImpl(new ProfileServiceImpl(), defaultConstraints());

        DecisionResponse response = decisionService.calculateDecision(buildRequest("00000000000", 2000, 12));

        assertEquals(Decision.NEGATIVE, response.decision());
        assertNull(response.approvedAmount());
        assertNull(response.approvedPeriod());
    }

    @Test
    void calculateDecisionAppliesMaximumCapForSelectedPeriod() {
        DecisionServiceImpl decisionService = new DecisionServiceImpl(
                personalCode -> new PersonProfile(1000, false),
                defaultConstraints()
        );

        DecisionResponse response = decisionService.calculateDecision(buildRequest("49002010987", 2000, 20));

        assertEquals(Decision.POSITIVE, response.decision());
        assertEquals(10000, response.approvedAmount());
        assertEquals(20, response.approvedPeriod());
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
