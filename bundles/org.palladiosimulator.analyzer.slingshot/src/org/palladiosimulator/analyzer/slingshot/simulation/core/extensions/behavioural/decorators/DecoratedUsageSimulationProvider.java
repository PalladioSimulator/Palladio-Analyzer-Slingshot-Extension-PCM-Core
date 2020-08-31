package org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.behavioural.decorators;

import org.palladiosimulator.analyzer.slingshot.repositories.UsageModelRepository;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.SimulatedUserProvider;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.UsageSimulationImpl;

/*
 * TODO: An extension is typically defined in its own (Eclipse-) project. Therefore, another way to provide such
 * 		 decorated classes must be found, for example by annotations that can be found at compile time and then
 * 		 generates such classes on the fly.
 */
public class DecoratedUsageSimulationProvider extends AbstractDecoratedSimulationBehaviorProvider {

	private final SimulatedUserProvider simulatedUserProvider;
	private final UsageModelRepository usageModelRepository;

	public DecoratedUsageSimulationProvider(final UsageModelRepository usageModelRepo,
			final SimulatedUserProvider simulatedUserProv) {
		simulatedUserProvider = simulatedUserProv;
		usageModelRepository = usageModelRepo;
	}

	@Override
	protected Class<?> getToBeDecoratedClazz() {
		return UsageSimulationImpl.class;
	}

	@Override
	protected Class<?>[] getConstructorArgumentsClazzes() {
		return new Class[] { UsageModelRepository.class, SimulatedUserProvider.class };
	}

	@Override
	protected Object[] getConstructorInstances() {
		return new Object[] { usageModelRepository, simulatedUserProvider };
	}

}
