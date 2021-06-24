package org.palladiosimulator.analyzer.slingshot.simulation.core.events;

import org.palladiosimulator.analyzer.slingshot.simulation.events.AbstractEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.EventContract;

/**
 * A simulation finished event will notify the system that the simulation has
 * officially ended, was successful and will not dispatch any further events.
 * 
 * @author Julijan Katic
 */
@EventContract(allowedCausers = {}, maximalPublishing = 1)
public class SimulationFinished extends AbstractEvent {

	public SimulationFinished() {
		super(SimulationFinished.class, 0);
	}

	public SimulationFinished(final double delay) {
		super(SimulationFinished.class, delay);
	}

}
