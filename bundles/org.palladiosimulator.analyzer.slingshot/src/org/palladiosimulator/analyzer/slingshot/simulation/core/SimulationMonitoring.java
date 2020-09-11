package org.palladiosimulator.analyzer.slingshot.simulation.core;

import java.util.List;

import org.palladiosimulator.analyzer.slingshot.simulation.core.entities.IUser;

public interface SimulationMonitoring {

	void addSimulatedUser();
	
	List<IUser> getSimulatedUsers();

}
