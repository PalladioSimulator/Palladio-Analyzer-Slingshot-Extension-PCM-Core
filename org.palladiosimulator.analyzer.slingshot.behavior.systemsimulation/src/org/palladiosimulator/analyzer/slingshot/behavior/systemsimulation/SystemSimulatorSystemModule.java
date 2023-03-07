package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation;

import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.ui.SystemSimulatorArchitectureModelTabConfigurer;
import org.palladiosimulator.analyzer.slingshot.core.extension.AbstractSlingshotExtension;

public class SystemSimulatorSystemModule extends AbstractSlingshotExtension {

	@Override
	protected void configure() {
		install(SystemSimulatorArchitectureModelTabConfigurer.class);
	}

	@Override
	public String getName() {
		return SystemSimulatorSystemModule.class.getSimpleName();
	}

}
