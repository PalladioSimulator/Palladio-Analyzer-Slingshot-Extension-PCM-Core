package org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl;

import org.palladiosimulator.pcm.usagemodel.ClosedWorkload;
import org.palladiosimulator.pcm.usagemodel.OpenWorkload;
import org.palladiosimulator.pcm.usagemodel.ScenarioBehaviour;
import org.palladiosimulator.pcm.usagemodel.UsageScenario;
import org.palladiosimulator.pcm.usagemodel.Workload;

public class UsageInterpretationContext {
	
	private final UsageScenario scenario;
	
	public UsageInterpretationContext(final UsageScenario scenario) {
		super();
		this.scenario = scenario;	
	}
	
	public UsageScenario getUsageScenario() {
		return scenario;
	}

	public ScenarioBehaviour getScenarioBehaviour() {
		return this.scenario.getScenarioBehaviour_UsageScenario();
	}
	
	public boolean isClosedWorkload() {
		return this.scenario.getWorkload_UsageScenario() instanceof ClosedWorkload;
	}
	
	public Workload getWorkload()  {
		return this.scenario.getWorkload_UsageScenario();
	}
	
	public boolean isOpenWorkload() {
		return this.scenario.getWorkload_UsageScenario() instanceof OpenWorkload;
	}
}
