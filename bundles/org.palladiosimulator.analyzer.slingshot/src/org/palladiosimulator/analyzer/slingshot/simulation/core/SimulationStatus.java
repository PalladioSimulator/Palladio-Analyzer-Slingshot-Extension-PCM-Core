package org.palladiosimulator.analyzer.slingshot.simulation.core;

import java.util.ArrayList;
import java.util.List;

import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.SimulatedUser;

public class SimulationStatus implements SimulationMonitoring {
	
	private List<SimulatedUser> simulatedUsers;
	
	
	public SimulationStatus(final List<SimulatedUser> simulatedUsers) {
		this.simulatedUsers = new ArrayList<SimulatedUser>(simulatedUsers);
	}
	


	@Override
	public void addSimulatedUser() {
		// TODO Auto-generated method stub
		
	}



	@Override
	public List<SimulatedUser> getSimulatedUsers() {
		return simulatedUsers;
	}

}
