package com.inbank.decision_engine;

import com.inbank.decision_engine.config.LoanConstraintsProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(LoanConstraintsProperties.class)
public class DecisionEngineApplication {

	public static void main(String[] args) {
		SpringApplication.run(DecisionEngineApplication.class, args);
	}

}
