package com.inbank.decision_engine.service.impl;

import com.inbank.decision_engine.config.LoanConstraintsProperties;
import com.inbank.decision_engine.dto.DecisionRequest;
import com.inbank.decision_engine.dto.DecisionResponse;
import com.inbank.decision_engine.model.Decision;
import com.inbank.decision_engine.service.DecisionService;
import com.inbank.decision_engine.service.ProfileService;
import org.springframework.stereotype.Service;

@Service
public class DecisionServiceImpl implements DecisionService {

    private final ProfileService profileService;
    private final LoanConstraintsProperties constraints;

    public DecisionServiceImpl(ProfileService profileService,
                               LoanConstraintsProperties loanConstraintsProperties) {
        this.profileService = profileService;
        this.constraints = loanConstraintsProperties;
    }

    /**
     * Decision flow:
     * 1. Reject if client has debt
     * 2. Try to approve using selected period
     * 3. If not possible, find smallest period that satisfies requested amount
     * 4. If still not possible, return maximum possible offer
     * 5. Otherwise reject
     */
    @Override
    public DecisionResponse calculateDecision(DecisionRequest request) {
        var profile = profileService.resolveProfile(request.getPersonalCode());

        // Business rule: clients with existing debt are not eligible for any loan
        if (profile.hasDebt()) {
            return createNegativeDecision();
        }

        int requestedAmount = request.getLoanAmount();
        int requestedPeriod = request.getLoanPeriod();
        int creditModifier = profile.creditModifier();

        // Calculate the maximum amount the client can get for the selected period
        int maxApprovedAmountForPeriod = calculateMaxApprovedAmountForPeriod(creditModifier, requestedPeriod);

        // If selected period already supports requested amount,
        // return the maximum possible amount for this period (not just requested amount)
        if (maxApprovedAmountForPeriod >= requestedAmount) {
            return new DecisionResponse(
                    Decision.POSITIVE,
                    maxApprovedAmountForPeriod,
                    requestedPeriod
            );
        }

        // Try to find the smallest period that can satisfy the requested amount.
        Integer suitablePeriod = findSuitablePeriodForRequestedAmount(creditModifier, requestedAmount);
        if (suitablePeriod != null) {
            int approvedAmount = calculateMaxApprovedAmountForPeriod(creditModifier, suitablePeriod);
            return new DecisionResponse(
                    Decision.POSITIVE,
                    approvedAmount,
                    suitablePeriod
            );
        }

        // If no period can satisfy the requested amount,
        // return the largest possible loan we can offer at all (using max period)
        int largestPossibleAmount = calculateMaxApprovedAmountForPeriod(creditModifier, constraints.getMaxPeriod());
        if (largestPossibleAmount >= constraints.getMinAmount()) {
            return new DecisionResponse(
                    Decision.POSITIVE,
                    largestPossibleAmount,
                    constraints.getMaxPeriod()
            );
        }

        return createNegativeDecision();
    }

    private int calculateMaxApprovedAmountForPeriod(int creditModifier, int period) {
        return Math.min(creditModifier * period, constraints.getMaxAmount());
    }

    private Integer findSuitablePeriodForRequestedAmount(int creditModifier, int requestedAmount) {
        for (int period = constraints.getMinPeriod(); period <= constraints.getMaxPeriod(); period++) {
            int maxApprovedAmount = calculateMaxApprovedAmountForPeriod(creditModifier, period);
            if (maxApprovedAmount >= requestedAmount) {
                return period;
            }
        }
        return null;
    }

    private DecisionResponse createNegativeDecision() {
        return new DecisionResponse(Decision.NEGATIVE, null, null);
    }
}
