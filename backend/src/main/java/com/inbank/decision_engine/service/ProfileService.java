package com.inbank.decision_engine.service;

import com.inbank.decision_engine.model.PersonProfile;

public interface ProfileService {
    PersonProfile resolveProfile(String personalCode);
}
