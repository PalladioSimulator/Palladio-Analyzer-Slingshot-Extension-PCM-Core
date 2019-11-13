package org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl;

import org.palladiosimulator.pcm.usagemodel.AbstractUserAction;
import org.palladiosimulator.pcm.usagemodel.UsageScenario;

public class SimulatedUser {
	
	private UsageScenario scenario;
	private AbstractUserAction currentPosition;
	
	public SimulatedUser(final UsageScenario scenario, final AbstractUserAction currentPosition) {
		this.scenario = scenario;
		this.currentPosition = currentPosition;
	}
	

	public AbstractUserAction currentPosition() {
		return currentPosition;
	}

	public UsageScenario currentScenario() {
		return scenario;
	}

}
