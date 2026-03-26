CREATE TABLE IF NOT EXISTS person_profile (
    personal_code VARCHAR(11) PRIMARY KEY,
    credit_modifier INT NOT NULL,
    has_debt BOOLEAN NOT NULL
);