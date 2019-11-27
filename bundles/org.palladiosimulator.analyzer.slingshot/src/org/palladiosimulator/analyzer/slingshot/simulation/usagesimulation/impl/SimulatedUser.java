package org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl;

import java.util.UUID;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.repositories.UsageModelRepository;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.events.EventObserver;
import org.palladiosimulator.pcm.usagemodel.AbstractUserAction;
import org.palladiosimulator.pcm.usagemodel.Delay;
import org.palladiosimulator.pcm.usagemodel.Stop;
import org.palladiosimulator.pcm.usagemodel.UsageScenario;

public class SimulatedUser {
	
	private final Logger LOGGER = Logger.getLogger(SimulatedUser.class);
	
	private String userId;
	private String userName;
	private UsageScenario scenario;
	private AbstractUserAction currentPosition;
	
	private UsageModelRepository usageModelRepository;
	
	public SimulatedUser(String userName, final UsageScenario scenario, final AbstractUserAction currentPosition, UsageModelRepository usageModelRepository) {
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


	public void nextEvent() {
		// TODO Auto-generated method stub
		// depending on the current position go back to the usage scenario and find the next event
		// how to determine the timeslot ?
		AbstractUserAction nextAction = usageModelRepository.findNextAction(scenario, currentPosition);
		
		if (null == nextAction) {
			LOGGER.info(String.format("SimulatedUser['%s'|'%s']: no nextEvent found.", userName, userId));
		} else {
			LOGGER.info(String.format("SimulatedUser['%s'|'%s']: nextEvent '%s' found", userName, userId, nextAction.getEntityName()));
			currentPosition = nextAction;
			
			// switch case: 
			// Delay Action
			if (nextAction instanceof Delay) {
				LOGGER.info(String.format("SimulatedUser['%s'|'%s']: scheduled DelayEvent", userName, userId));
			} else if (nextAction instanceof Stop) {
				LOGGER.info(String.format("SimulatedUser['%s'|'%s']: scheduled StopEvent", userName, userId));
			}
			
		}
		
		// notify simulation driver to schedule next event
		
	}

}
