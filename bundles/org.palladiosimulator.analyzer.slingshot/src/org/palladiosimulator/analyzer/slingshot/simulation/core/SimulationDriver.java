package org.palladiosimulator.analyzer.slingshot.simulation.core;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.simulation.api.Simulation;
import org.palladiosimulator.analyzer.slingshot.simulation.engine.SimulationEngine;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.SimulatedUser;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.UsageSimulation;
import org.palladiosimulator.pcm.usagemodel.UsageModel;

public class SimulationDriver implements Simulation {
	
	private final Logger LOGGER = Logger.getLogger(SimulationDriver.class);
	
	//FIXME: part of UsageSimulation or part of simulationDriver ?
	private List<SimulatedUser> simulatedUsers;
	
	private UsageSimulation usageSimulation;
	private SimulationEngine simEngine;
	
	
	public SimulationDriver(final UsageSimulation usageSimulation, final SimulationEngine simEngine) {
		this.simulatedUsers = new ArrayList<SimulatedUser>();
		this.usageSimulation = usageSimulation;
		this.simEngine = simEngine;
	}

	public void init(final UsageModel usageModel) {
		LOGGER.info("Start simulation driver initialization.");
		
		usageSimulation.loadModel(usageModel);
		simulatedUsers.addAll(usageSimulation.createSimulatedUsers());
		LOGGER.info(String.format("Created '%s' users for closed workload simulation", simulatedUsers.size()));
		
		scheduleUserStartEvents();
		
		LOGGER.info("Finished simulation driver initialization.");
	}
	
	private void scheduleUserStartEvents() {
		for (SimulatedUser simulatedUser : simulatedUsers) {
			DESEvent startUserEvent = usageSimulation.findStartEvent(simulatedUser);
			simEngine.scheduleEvent(startUserEvent);
		}
	}
	
	public void startSimulation() {
		simEngine.start();
	}
	
	

	public SimulationMonitoring monitorSimulation() {
		return new SimulationStatus(simulatedUsers);
	}

}
