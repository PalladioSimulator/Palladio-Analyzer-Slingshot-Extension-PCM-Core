package org.palladiosimulator.analyzer.slingshot.behavior.usagemodel;

import org.palladiosimulator.pcm.usagemodel.ClosedWorkload;
import org.palladiosimulator.pcm.usagemodel.OpenWorkload;
import org.palladiosimulator.pcm.usagemodel.ScenarioBehaviour;
import org.palladiosimulator.pcm.usagemodel.UsageScenario;
import org.palladiosimulator.pcm.usagemodel.Workload;

public class UsageInterpretationContext {

	private final UsageScenario scenario;
	
	public UsageInterpretationContext(final UsageScenario scenario) {
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
	
	public boolean isOpenWorkload() {
		return this.scenario.getWorkload_UsageScenario() instanceof OpenWorkload;
	}
	
	public Workload getWorkload() {
		return this.scenario.getWorkload_UsageScenario();
	}
}
