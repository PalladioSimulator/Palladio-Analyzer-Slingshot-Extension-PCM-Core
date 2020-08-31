package org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.behavioural.decorators;

import org.palladiosimulator.analyzer.slingshot.simulation.systemsimulation.impl.SystemSimulationImpl;

public class DecoratedSystemBehaviorProvider extends AbstractDecoratedSimulationBehaviorProvider {

	@Override
	protected Class<?> getToBeDecoratedClazz() {
		return SystemSimulationImpl.class;
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
