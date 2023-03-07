package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation;

import org.palladiosimulator.analyzer.slingshot.core.extension.AbstractSlingshotExtension;
import org.palladiosimulator.pcm.allocation.Allocation;

public class SystemSimulatorModule extends AbstractSlingshotExtension {

	@Override
	protected void configure() {
		install(SeffSimulationBehavior.class);
		install(SystemSimulationBehavior.class);
		provideModel(Allocation.class, AllocationModelProvider.class);
	}

	@Override
	public String getName() {
		return SystemSimulatorModule.class.getSimpleName();
	}

}
