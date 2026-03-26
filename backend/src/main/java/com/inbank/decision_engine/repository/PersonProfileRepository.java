package com.inbank.decision_engine.repository;

import com.inbank.decision_engine.model.PersonProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonProfileRepository extends JpaRepository<PersonProfileEntity, String> {
}