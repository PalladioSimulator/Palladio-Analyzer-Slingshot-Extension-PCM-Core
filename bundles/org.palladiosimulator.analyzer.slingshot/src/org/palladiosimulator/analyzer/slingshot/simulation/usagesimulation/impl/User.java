package org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl;

import java.util.UUID;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.repositories.UsageModelRepository;
import org.palladiosimulator.pcm.usagemodel.AbstractUserAction;
import org.palladiosimulator.pcm.usagemodel.UsageScenario;

public class User {
	
	private final Logger LOGGER = Logger.getLogger(User.class);
	
	private String userId;
	private String userName;
	private UsageScenario scenario;
	private AbstractUserAction currentPosition;
	
	private UsageModelRepository usageModelRepository;
	
	public User(String userName, final UsageScenario scenario, final AbstractUserAction currentPosition, UsageModelRepository usageModelRepository) {
		this.userId = UUID.randomUUID().toString();
		this.userName = userName;
		this.scenario = scenario;
		this.currentPosition = currentPosition;
		this.usageModelRepository = usageModelRepository;
	}
	

	public AbstractUserAction currentPosition() {
		return currentPosition;
	}

	public UsageScenario currentScenario() {
		return scenario;
	}

	public AbstractUserAction nextAction() {
		// how to determine the timeslot ?
		AbstractUserAction nextAction = usageModelRepository.findNextAction(scenario, currentPosition);
		currentPosition = nextAction;	
		return nextAction;		
	}


	public String getUserName() {
		return userName;
	}


	public String getUserId() {
		return userId;
	}

}
