
package org.palladiosimulator.analyzer.slingshot.simulation.engine;

import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.events.Dispatcher;

import com.google.common.eventbus.EventBus;

public interface SimulationEngine {
	
	void start();
	
	void scheduleEvent(DESEvent e);
	
	// returns the simulated time
	void getTime();

	boolean hasScheduledEvents();
	
	//FIXME:: Assumption the SimulationEngine is capable of providing its EventDispatcher
	//This needs later to be evaluated.
	EventBus getEventDispatcher();
}
