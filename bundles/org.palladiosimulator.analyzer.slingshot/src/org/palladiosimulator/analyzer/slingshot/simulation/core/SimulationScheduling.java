package org.palladiosimulator.analyzer.slingshot.simulation.core;

import java.util.List;

import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;

public interface SimulationScheduling {
	
	void scheduleForSimulation(DESEvent event);
	void scheduleForSimulation(List<DESEvent> events);

}
