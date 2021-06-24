package org.palladiosimulator.analyzer.slingshot.simulation.api;

import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;

/**
 * The simulation monitoring interface allows for event-based probes to be
 * tracked and register calculators.
 * 
 * @author Julijan Katic
 *
 */
public interface SimulationMonitoring {

	/**
	 * Publishes an event that can be probed and used later in calculations.
	 * 
	 * @param event The event that can be probed.
	 */
	void publishProbeEvent(final DESEvent event);

}