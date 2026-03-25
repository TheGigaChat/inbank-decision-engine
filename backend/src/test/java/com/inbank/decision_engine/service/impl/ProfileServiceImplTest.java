package com.inbank.decision_engine.service.impl;

import com.inbank.decision_engine.model.PersonProfile;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProfileServiceImplTest {

    private final ProfileServiceImpl profileService = new ProfileServiceImpl();

    @Test
    void resolveProfileReturnsExpectedProfileForModifier100Code() {
        PersonProfile profile = profileService.resolveProfile("49002010965");

        assertEquals(100, profile.creditModifier());
        assertFalse(profile.hasDebt());
    }

    @Test
    void resolveProfileReturnsExpectedProfileForModifier300Code() {
        PersonProfile profile = profileService.resolveProfile("49002010976");

        assertEquals(300, profile.creditModifier());
        assertFalse(profile.hasDebt());
    }

    @Test
    void resolveProfileReturnsExpectedProfileForModifier1000Code() {
        PersonProfile profile = profileService.resolveProfile("49002010987");

        assertEquals(1000, profile.creditModifier());
        assertFalse(profile.hasDebt());
    }

    @Test
    void resolveProfileReturnsDebtProfileForDebtCode() {
        PersonProfile profile = profileService.resolveProfile("49002010998");

        assertEquals(0, profile.creditModifier());
        assertTrue(profile.hasDebt());
    }

    @Test
    void resolveProfileReturnsFallbackProfileForUnknownCode() {
        PersonProfile profile = profileService.resolveProfile("00000000000");

        assertEquals(0, profile.creditModifier());
        assertFalse(profile.hasDebt());
    }
}
