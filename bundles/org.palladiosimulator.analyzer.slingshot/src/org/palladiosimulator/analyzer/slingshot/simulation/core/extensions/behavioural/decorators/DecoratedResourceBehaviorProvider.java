package org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.behavioural.decorators;

import org.palladiosimulator.analyzer.slingshot.simulation.resourcesimulation.impl.ResourceSimulationImpl;

public class DecoratedResourceBehaviorProvider extends AbstractDecoratedSimulationBehaviorProvider {

	@Override
	protected Class<?> getToBeDecoratedClazz() {
		return ResourceSimulationImpl.class;
	}

	@Override
	protected Class<?>[] getConstructorArgumentsClazzes() {
		return new Class[] {};
	}

	@Override
	protected Object[] getConstructorInstances() {
		return new Object[] {};
	}

}
