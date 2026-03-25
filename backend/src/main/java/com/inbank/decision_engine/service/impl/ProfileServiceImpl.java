package com.inbank.decision_engine.service.impl;

import com.inbank.decision_engine.model.PersonProfile;
import com.inbank.decision_engine.service.ProfileService;
import org.springframework.stereotype.Service;

@Service
public class ProfileServiceImpl implements ProfileService {

    @Override
    public PersonProfile resolveProfile(String personalCode) {
        return switch (personalCode) {
            case "49002010965" -> new PersonProfile(100, false);
            case "49002010976" -> new PersonProfile(300, false);
            case "49002010987" -> new PersonProfile(1000, false);
            case "49002010998" -> new PersonProfile(0, true);
            default -> new PersonProfile(0, false);
        };
    }
}
