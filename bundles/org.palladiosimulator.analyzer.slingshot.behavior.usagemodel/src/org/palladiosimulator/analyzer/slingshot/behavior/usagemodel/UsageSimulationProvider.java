package org.palladiosimulator.analyzer.slingshot.behavior.usagemodel;

import org.palladiosimulator.analyzer.slingshot.repositories.UsageModelRepository;
import org.palladiosimulator.analyzer.slingshot.repositories.impl.UsageModelRepositoryImpl;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.behavioural.annotations.Extension;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.behavioural.decorators.AbstractDecoratedSimulationBehaviorProvider;

@Extension
public class UsageSimulationProvider extends AbstractDecoratedSimulationBehaviorProvider {
	
	/*
	 * TODO: Better dependency support for such scenarios. Maybe a Dependency Injector is better instead of directly initialising it within the class.
	 */
	private final UsageModelRepository usageModelRepository = new UsageModelRepositoryImpl();
	private final SimulatedUserProvider simulatedUserProvider = new SimulatedUserProvider();
	
	@Override
	protected Class<?> getToBeDecoratedClazz() {
		return UsageSimulationImpl.class;
	}

	@Override
	protected Class<?>[] getConstructorArgumentsClazzes() {
		return new Class<?>[] {UsageModelRepository.class, SimulatedUserProvider.class};
	}

	@Override
	protected Object[] getConstructorInstances() {
		// TODO Auto-generated method stub
		return new Object[] {usageModelRepository, simulatedUserProvider};
	}
	

}
