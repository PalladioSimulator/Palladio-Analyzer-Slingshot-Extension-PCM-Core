
package org.palladiosimulator.analyzer.slingshot.simulation.engine;

import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;

public interface SimulationEngine {
	
	void start();
	
	void scheduleEvent(DESEvent e);
	
	// returns the simulated time
	void getTime();

}
