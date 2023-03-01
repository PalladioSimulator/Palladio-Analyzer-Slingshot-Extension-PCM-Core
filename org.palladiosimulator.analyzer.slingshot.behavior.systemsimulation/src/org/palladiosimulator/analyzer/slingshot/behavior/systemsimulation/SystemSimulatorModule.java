package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation;

import org.palladiosimulator.analyzer.slingshot.core.extension.AbstractSlingshotExtension;

public class SystemSimulatorModule extends AbstractSlingshotExtension {

	@Override
	protected void configure() {
		install(SeffSimulationBehavior.class);
		install(SystemSimulationBehavior.class);
	}

	@Override
	public String getName() {
		return SystemSimulatorModule.class.getSimpleName();
	}

}
