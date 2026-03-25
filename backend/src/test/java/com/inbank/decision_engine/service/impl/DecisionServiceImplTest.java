package com.inbank.decision_engine.service.impl;

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
        DecisionServiceImpl decisionService = new DecisionServiceImpl(profileService);

        DecisionResponse response = decisionService.calculateDecision(buildRequest("49002010998"));

        assertEquals(Decision.NEGATIVE, response.decision());
        assertNull(response.approvedAmount());
        assertNull(response.approvedPeriod());
    }

    private DecisionRequest buildRequest(String personalCode) {
        DecisionRequest request = new DecisionRequest();
        request.setPersonalCode(personalCode);
        request.setLoanAmount(2000);
        request.setLoanPeriod(12);
        return request;
    }
}
