package org.palladiosimulator.analyzer.slingshot.simulation.core.events;

import org.palladiosimulator.analyzer.slingshot.simulation.events.AbstractEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.EventContract;

/**
 * This special event will be spawned indicating that the simulation has
 * started. This is a special event: No other event can cause this and this is
 * allowed to be published to the event bus only once.
 * 
 * @author Julijan Katic
 */
@EventContract(maximalPublishing = 1, allowedCausers = {})
public class SimulationStarted extends AbstractEvent {

	public SimulationStarted() {
		super(SimulationStarted.class, 0);
	}

}
