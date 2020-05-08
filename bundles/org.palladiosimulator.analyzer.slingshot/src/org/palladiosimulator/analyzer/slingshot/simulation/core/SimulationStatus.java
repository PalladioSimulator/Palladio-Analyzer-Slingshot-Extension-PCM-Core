package org.palladiosimulator.analyzer.slingshot.simulation.core;

import java.util.ArrayList;
import java.util.List;

import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.User;

public class SimulationStatus implements SimulationMonitoring {
	
	private List<User> simulatedUsers;
	
	
	public SimulationStatus(final List<User> simulatedUsers) {
		this.simulatedUsers = new ArrayList<User>(simulatedUsers);
	}
	


	@Override
	public void addSimulatedUser() {
		
	}



	@Override
	public List<User> getSimulatedUsers() {
		return simulatedUsers;
	}

}
