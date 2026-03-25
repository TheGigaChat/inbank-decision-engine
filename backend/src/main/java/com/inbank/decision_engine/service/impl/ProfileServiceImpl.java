package com.inbank.decision_engine.service.impl;

import com.inbank.decision_engine.model.PersonProfile;
import com.inbank.decision_engine.service.ProfileService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ProfileServiceImpl implements ProfileService {

    private static final Map<String, PersonProfile> PERSON_PROFILES = Map.of(
            "49002010965", new PersonProfile(100, false),
            "49002010976", new PersonProfile(300, false),
            "49002010987", new PersonProfile(1000, false),
            "49002010998", new PersonProfile(0, true)
    );

    /**
     * Resolves client profile based on personal code.
     * Returns predefined credit modifier and debt status.
     * If personal code is unknown, defaults to a profile with no debt and zero credit modifier.
     */
    @Override
    public PersonProfile resolveProfile(String personalCode) {
        return PERSON_PROFILES.getOrDefault(personalCode, new PersonProfile(0, false));
    }
}
