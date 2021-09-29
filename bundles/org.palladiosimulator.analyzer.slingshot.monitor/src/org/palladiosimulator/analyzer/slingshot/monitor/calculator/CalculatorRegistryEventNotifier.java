package org.palladiosimulator.analyzer.slingshot.monitor.calculator;

import javax.inject.Inject;

import org.palladiosimulator.analyzer.slingshot.monitor.data.CalculatorRegistered;
import org.palladiosimulator.analyzer.slingshot.simulation.api.SimulationScheduling;
import org.palladiosimulator.probeframework.calculator.Calculator;
import org.palladiosimulator.probeframework.calculator.CalculatorRegistryListener;

/**
 * This class is used to convert a notification of a Calculator Registration to
 * an event.
 * 
 * @author Julijan Katic
 */
public final class CalculatorRegistryEventNotifier implements CalculatorRegistryListener {

	private final SimulationScheduling simulationScheduling;

	@Inject
	public CalculatorRegistryEventNotifier(final SimulationScheduling scheduling) {
		this.simulationScheduling = scheduling;
	}

	@Override
	public void notifyCalculatorRegistration(final Calculator calculator) {
		this.simulationScheduling.scheduleForSimulation(new CalculatorRegistered(calculator));
	}

}
