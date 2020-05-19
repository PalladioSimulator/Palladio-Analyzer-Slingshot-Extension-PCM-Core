package org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.decorators;

import org.palladiosimulator.analyzer.slingshot.simulation.resourcesimulation.impl.ResourceSimulationImpl;
import org.palladiosimulator.analyzer.slingshot.simulation.systemsimulation.impl.SystemSimulationImpl;

public class DecoratedSystemBehaviorProvider extends AbstractDecoratedSimulationBehaviorProvider {

	@Override
	Class<?> getToBeDecoratedClazz() {
		return SystemSimulationImpl.class;
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
