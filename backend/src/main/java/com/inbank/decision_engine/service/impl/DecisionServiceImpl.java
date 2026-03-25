package com.inbank.decision_engine.service.impl;

import com.inbank.decision_engine.dto.DecisionRequest;
import com.inbank.decision_engine.dto.DecisionResponse;
import com.inbank.decision_engine.model.Decision;
import com.inbank.decision_engine.service.DecisionService;
import com.inbank.decision_engine.service.ProfileService;
import org.springframework.stereotype.Service;

@Service
public class DecisionServiceImpl implements DecisionService {

    private static final int MIN_AMOUNT = 2000;
    private static final int MAX_AMOUNT = 10000;
    private static final int MIN_PERIOD = 12;
    private static final int MAX_PERIOD = 60;

    private final ProfileService profileService;

    public DecisionServiceImpl(ProfileService profileService) {
        this.profileService = profileService;
    }

    @Override
    public DecisionResponse calculateDecision(DecisionRequest request) {
        var profile = profileService.resolveProfile(request.getPersonalCode());

        if (profile.hasDebt()) {
            return new DecisionResponse(Decision.NEGATIVE, null, null);
        }

        int requestedAmount = request.getLoanAmount();
        int requestedPeriod = request.getLoanPeriod();
        int creditModifier = profile.creditModifier();

        int maxApprovedAmountForPeriod = calculateMaxApprovedAmountForPeriod(creditModifier, requestedPeriod);

        if (maxApprovedAmountForPeriod >= requestedAmount) {
            return new DecisionResponse(
                    Decision.POSITIVE,
                    maxApprovedAmountForPeriod,
                    requestedPeriod
            );
        }

        Integer suitablePeriod = findSuitablePeriodForRequestedAmount(creditModifier, requestedAmount);
        if (suitablePeriod != null) {
            int approvedAmount = calculateMaxApprovedAmountForPeriod(creditModifier, suitablePeriod);
            return new DecisionResponse(
                    Decision.POSITIVE,
                    approvedAmount,
                    suitablePeriod
            );
        }

        int largestPossibleAmount = calculateMaxApprovedAmountForPeriod(creditModifier, MAX_PERIOD);
        if (largestPossibleAmount >= MIN_AMOUNT) {
            return new DecisionResponse(
                    Decision.POSITIVE,
                    largestPossibleAmount,
                    MAX_PERIOD
            );
        }

        return new DecisionResponse(Decision.NEGATIVE, null, null);
    }

    private int calculateMaxApprovedAmountForPeriod(int creditModifier, int period) {
        return Math.min(creditModifier * period, MAX_AMOUNT);
    }

    private Integer findSuitablePeriodForRequestedAmount(int creditModifier, int requestedAmount) {
        for (int period = MIN_PERIOD; period <= MAX_PERIOD; period++) {
            int maxApprovedAmount = calculateMaxApprovedAmountForPeriod(creditModifier, period);
            if (maxApprovedAmount >= requestedAmount) {
                return period;
            }
        }
        return null;
    }
}
