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
     * 2. Calculate the maximum approvable amount for the selected period
     * 3. If selected period produces a valid loan amount, return it
     * 4. Otherwise, try to find the smallest new period that produces a valid loan amount
     * 5. If no valid period exists, reject
     */
    @Override
    public DecisionResponse calculateDecision(DecisionRequest request) {
        var profile = profileService.resolveProfile(request.getPersonalCode());

        // Business rule: clients with existing debt are not eligible for any loan
        if (profile.hasDebt()) {
            return createNegativeDecision();
        }

        int requestedPeriod = request.getLoanPeriod();
        int creditModifier = profile.creditModifier();

        // First, determine the maximum amount we can offer within the selected period.
        int approvedAmountForSelectedPeriod =
                calculateMaxApprovedAmountForPeriod(creditModifier, requestedPeriod);

        // If selected period already produces a valid loan amount, return it immediately,
        // even if it is lower than the requested amount.
        if (approvedAmountForSelectedPeriod >= constraints.getMinAmount()) {
            int optimizedPeriod =
                    findSmallestPeriodForApprovedAmount(creditModifier, approvedAmountForSelectedPeriod);

            return new DecisionResponse(
                    Decision.POSITIVE,
                    approvedAmountForSelectedPeriod,
                    optimizedPeriod
            );
        }

        // If selected period cannot produce even the minimum valid amount,
        // try to find the smallest period that does.
        Integer suitablePeriod = findSuitablePeriodForMinimumAmount(creditModifier);
        if (suitablePeriod != null) {
            int approvedAmount =
                    calculateMaxApprovedAmountForPeriod(creditModifier, suitablePeriod);

            return new DecisionResponse(
                    Decision.POSITIVE,
                    approvedAmount,
                    suitablePeriod
            );
        }

        return createNegativeDecision();
    }

    private int calculateMaxApprovedAmountForPeriod(int creditModifier, int period) {
        return Math.min(creditModifier * period, constraints.getMaxAmount());
    }

    private Integer findSuitablePeriodForMinimumAmount(int creditModifier) {
        for (int period = constraints.getMinPeriod(); period <= constraints.getMaxPeriod(); period++) {
            int approvedAmount = calculateMaxApprovedAmountForPeriod(creditModifier, period);
            if (approvedAmount >= constraints.getMinAmount()) {
                return period;
            }
        }
        return null;
    }

    private int findSmallestPeriodForApprovedAmount(int creditModifier, int approvedAmount) {
        for (int period = constraints.getMinPeriod(); period <= constraints.getMaxPeriod(); period++) {
            if (creditModifier * period >= approvedAmount) {
                return period;
            }
        }
        return constraints.getMaxPeriod();
    }

    private DecisionResponse createNegativeDecision() {
        return new DecisionResponse(Decision.NEGATIVE, null, null);
    }
}
