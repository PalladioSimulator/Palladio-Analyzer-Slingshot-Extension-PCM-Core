package org.palladiosimulator.analyzer.slingshot.behavior.usagesimulation;

import org.palladiosimulator.analyzer.slingshot.core.extension.AbstractSlingshotExtension;

public class UsageSimulationModule extends AbstractSlingshotExtension {

	@Override
	protected void configure() {
		install(UsageSimulationBehavior.class);
	}

	@Override
	public String getName() {
		return UsageSimulationModule.class.getSimpleName();
	}
}
