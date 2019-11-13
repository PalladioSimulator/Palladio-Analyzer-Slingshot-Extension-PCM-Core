package org.palladiosimulator.analyzer.slingshot.simulation.core;

import java.util.List;

import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.SimulatedUser;

public interface SimulationMonitoring {

	void addSimulatedUser();
	
	List<SimulatedUser> getSimulatedUsers();

}
