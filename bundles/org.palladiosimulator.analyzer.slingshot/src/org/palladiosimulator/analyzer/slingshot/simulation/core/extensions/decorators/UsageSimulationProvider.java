package org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.decorators;

import org.palladiosimulator.analyzer.slingshot.repositories.UsageModelRepository;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.SimulatedUserProvider;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.UsageSimulationImpl;

public class UsageSimulationProvider extends AbstractDecoratedSimulationBehaviorProvider {

	private SimulatedUserProvider simulatedUserProvider;
	private UsageModelRepository usageModelRepository;

	public UsageSimulationProvider(UsageModelRepository usageModelRepo, SimulatedUserProvider simulatedUserProv) {
		simulatedUserProvider = simulatedUserProv;
		usageModelRepository = usageModelRepo;
	}

	@Override
	Class<?> getToBeDecoratedClazz() {
		return UsageSimulationImpl.class;
	}

	@Override
	Class<?>[] getConstructorArgumentsClazzes() {
		return new Class[] {UsageModelRepository.class,SimulatedUserProvider.class};
	}

	@Override
	Object[] getConstructorInstances() {
		return new Object[] {usageModelRepository,simulatedUserProvider};
	}

}
