package org.palladiosimulator.analyzer.slingshot.simulation.core;

import java.util.ArrayList;
import java.util.List;

import org.palladiosimulator.analyzer.slingshot.simulation.core.entities.IUser;

public class SimulationStatus implements SimulationMonitoring {
	
	private final List<IUser> simulatedUsers;
	
	
	public SimulationStatus(final List<IUser> simulatedUsers) {
		this.simulatedUsers = new ArrayList<IUser>(simulatedUsers);
	}
	


	@Override
	public void addSimulatedUser() {
		
	}



	@Override
	public List<IUser> getSimulatedUsers() {
		return simulatedUsers;
	}

}
