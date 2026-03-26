package com.inbank.decision_engine.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "person_profile")
public class PersonProfileEntity {
    @Id
    @Column(name = "personal_code", nullable = false, unique = true)
    private String personalCode;

    @Column(name = "credit_modifier", nullable = false)
    private Integer creditModifier;

    @Column(name = "has_debt", nullable = false)
    private boolean hasDebt;
}
