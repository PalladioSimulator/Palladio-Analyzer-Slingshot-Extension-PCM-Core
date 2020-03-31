package org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.decorators;

import org.palladiosimulator.analyzer.slingshot.simulation.resourcesimulation.impl.ResourceSimulationImpl;

public class DecoratedResourceBehaviorProvider extends AbstractDecoratedSimulationBehaviorProvider {

	@Override
	Class<?> getToBeDecoratedClazz() {
		return ResourceSimulationImpl.class;
	}

	@Override
	Class<?>[] getConstructorArgumentsClazzes() {
		return new Class[] {};
	}

	@Override
	Object[] getConstructorInstances() {
		return new Object[] {};
	}

}
