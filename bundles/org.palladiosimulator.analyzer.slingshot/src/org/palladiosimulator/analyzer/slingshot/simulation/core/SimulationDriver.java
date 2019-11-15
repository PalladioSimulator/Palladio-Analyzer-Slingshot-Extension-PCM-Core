package org.palladiosimulator.analyzer.slingshot.simulation.core;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.simulation.api.Simulation;
import org.palladiosimulator.analyzer.slingshot.simulation.engine.SimulationEngine;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.events.StartUserEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.SimulatedUser;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.SimulatedUserProvider;

public class SimulationDriver implements Simulation {
	
	private final Logger LOGGER = Logger.getLogger(SimulationDriver.class);
	
	private List<SimulatedUser> simulatedUsers;
	
	private SimulationEngine simEngine;
	private SimulatedUserProvider simulatedUsersProvider;
	
	
	public SimulationDriver(final SimulatedUserProvider simulatedUsersProvider, final SimulationEngine simEngine) {
		this.simulatedUsers = new ArrayList<SimulatedUser>();
		this.simEngine = simEngine;
		this.simulatedUsersProvider = simulatedUsersProvider;
	}

	public void init() {
		LOGGER.info("Start simulation driver initialization.");
		
		simulatedUsers.addAll(simulatedUsersProvider.createClosedWorkloadSimulatedUsers());
		LOGGER.info(String.format("Created '%s' users for closed workload simulation", simulatedUsers.size()));
		
		scheduleUserStartEvents();
		
		LOGGER.info("Finished simulation driver initialization.");
	}
	
	
	public void startSimulation() {
		simEngine.start();
		LOGGER.info("Simulation driver is running ......");
	}
	
	
	private void scheduleUserStartEvents() {
		for (SimulatedUser simulatedUser : simulatedUsers) {
			DESEvent startUserEvent = new StartUserEvent(simulatedUser);
			// schedule start event for users -> SimulationEngine.schedule(); -> hier schauen wie man AbstractSimEngine anstöpselt bzw. erstmal dummy classe
			simEngine.scheduleEvent(startUserEvent );
		}
		
	}

	public SimulationMonitoring monitorSimulation() {
		return new SimulationStatus(simulatedUsers);
	}

}
