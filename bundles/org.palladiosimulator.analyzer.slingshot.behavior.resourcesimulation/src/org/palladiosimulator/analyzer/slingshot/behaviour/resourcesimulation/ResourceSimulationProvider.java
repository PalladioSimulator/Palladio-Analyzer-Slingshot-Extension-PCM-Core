package org.palladiosimulator.analyzer.slingshot.behaviour.resourcesimulation;

import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.behavioural.decorators.AbstractDecoratedSimulationBehaviorProvider;

public class ResourceSimulationProvider extends AbstractDecoratedSimulationBehaviorProvider {

	
	@Override
	protected Class<?> getToBeDecoratedClazz() {
		// TODO Auto-generated method stub
		return ResourceSimulation.class;
	}

	@Override
	protected Class<?>[] getConstructorArgumentsClazzes() {
		// TODO Auto-generated method stub
		return new Class<?>[] {};
	}

	@Override
	protected Object[] getConstructorInstances() {
		// TODO Auto-generated method stub
		return new Object[] {};
	}

}
