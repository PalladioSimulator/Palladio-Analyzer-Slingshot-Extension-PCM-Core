package org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral;

import org.palladiosimulator.analyzer.slingshot.simulation.core.SimulationScheduling;

public abstract class AbstractSimulationBehaviorExtensionTest {

	protected final SimulationScheduling simulationScheduling;

	public AbstractSimulationBehaviorExtensionTest(final SimulationScheduling simulationScheduling) {
		this.simulationScheduling = simulationScheduling;
	}

}
