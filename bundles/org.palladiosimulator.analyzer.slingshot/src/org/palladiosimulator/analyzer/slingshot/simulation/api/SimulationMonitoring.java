package org.palladiosimulator.analyzer.slingshot.simulation.api;

import org.palladiosimulator.probeframework.ProbeFrameworkContext;
import org.palladiosimulator.probeframework.calculator.Calculator;
import org.palladiosimulator.probeframework.calculator.IGenericCalculatorFactory;

/**
 * The simulation monitoring interface allows for event-based probes to be
 * tracked and register calculators.
 * 
 * @author Julijan Katic
 *
 */
public interface SimulationMonitoring {

	/**
	 * Initializes the monitoring module.
	 */
	public void init();

	/**
	 * Returns a calculator factory for creating {@link Calculator}s.
	 * 
	 * @return The calculator factory.
	 */
	public IGenericCalculatorFactory getCalculatorFactory();

	/**
	 * Returns a Probe Framework context.
	 * 
	 * @return
	 */
	public ProbeFrameworkContext getProbeFrameworkContext();
}