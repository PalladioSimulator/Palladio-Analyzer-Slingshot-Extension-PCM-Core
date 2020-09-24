package org.palladiosimulator.analyzer.slingshot.behaviour.resourcesimulation;

import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.behavioral.decorators.AbstractDecoratedSimulationBehaviorProvider;

public class ResourceSimulationProvider extends AbstractDecoratedSimulationBehaviorProvider {

	@Override
	protected Class<?> getToBeDecoratedClazz() {
		return ResourceSimulation.class;
	}

}
