package com.inbank.decision_engine.service.impl;

import com.inbank.decision_engine.dto.DecisionRequest;
import com.inbank.decision_engine.dto.DecisionResponse;
import com.inbank.decision_engine.model.Decision;
import com.inbank.decision_engine.service.DecisionService;
import com.inbank.decision_engine.service.ProfileService;
import org.springframework.stereotype.Service;

@Service
public class DecisionServiceImpl implements DecisionService {

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

        return null;
    }
}
