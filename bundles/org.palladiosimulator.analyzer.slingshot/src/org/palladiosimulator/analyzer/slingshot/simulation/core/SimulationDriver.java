package org.palladiosimulator.analyzer.slingshot.simulation.core;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.repositories.UsageModelRepository;
import org.palladiosimulator.analyzer.slingshot.repositories.impl.UsageModelRepositoryImpl;
import org.palladiosimulator.analyzer.slingshot.simulation.api.Simulation;
import org.palladiosimulator.analyzer.slingshot.simulation.engine.SimulationEngine;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.SimulatedUser;
import org.palladiosimulator.pcm.usagemodel.AbstractUserAction;
import org.palladiosimulator.pcm.usagemodel.ClosedWorkload;
import org.palladiosimulator.pcm.usagemodel.OpenWorkload;
import org.palladiosimulator.pcm.usagemodel.UsageModel;
import org.palladiosimulator.pcm.usagemodel.UsageScenario;
import org.palladiosimulator.pcm.usagemodel.Workload;

public class SimulationDriver implements Simulation {
	
	private final Logger LOGGER = Logger.getLogger(SimulationDriver.class);
	
	private List<SimulatedUser> simulatedUsers;
	private UsageModelRepository usageModelRepository;
	private SimulationEngine simEngine;
	
	
	public SimulationDriver(SimulationEngine simEngine, final UsageModel usageModel) {
		this.simulatedUsers = new ArrayList<SimulatedUser>();
		this.simEngine = simEngine;
		this.usageModelRepository = new UsageModelRepositoryImpl(usageModel);
	}

	public void init() {
		LOGGER.info("Start simulation driver initialization.");
		
		// parse usage model
		List<UsageScenario> usageScenarios = usageModelRepository.findAllUsageScenarios();
		
		for (UsageScenario usageScenario : usageScenarios) {
			Workload workload = usageModelRepository.findWorkloadForUsageScenario(usageScenario);
			
			if (workload instanceof ClosedWorkload ) {
				LOGGER.info("Found closed workload");
				ClosedWorkload closedWorkload = (ClosedWorkload) workload;
				int population = closedWorkload.getPopulation();
				
				simulatedUsers.addAll(createUsersForClosedWorkload(usageScenario, population));
				LOGGER.info(String.format("Created '%s' users for closed workload simulation", simulatedUsers.size()));
				
				scheduleUserStartEvents();
				
			} else if (workload instanceof OpenWorkload) {
				LOGGER.info("Found open workload");
				LOGGER.info("FIXME: Open workload simulation currently not implemented.");
				
			} else {
				LOGGER.info("Found undefined workload");
			}
			
		}
		
		LOGGER.info("Finished simulation driver initialization.");
	}
	
	
	public void startSimulation() {
		simEngine.start();
		LOGGER.info("Ssimulation driver is running ......");
	}
	
	
	private void scheduleUserStartEvents() {
		for (SimulatedUser simulatedUser : simulatedUsers) {
			DESEvent startUserEvent = new StartUserEvent(simulatedUser);
			// schedule start event for users -> SimulationEngine.schedule(); -> hier schauen wie man AbstractSimEngine anstöpselt bzw. erstmal dummy classe
			simEngine.scheduleEvent(startUserEvent );
		}
		
	}

	private List<SimulatedUser> createUsersForClosedWorkload(final UsageScenario scenario, final int population) {
		List<SimulatedUser> simulatedUsers = new ArrayList<SimulatedUser>();
		for (int i = 0; i < population; i++) {
			AbstractUserAction currentPosition = usageModelRepository.findFirstActionOf(scenario);
			SimulatedUser user = new SimulatedUser(currentPosition.getEntityName(), scenario, currentPosition, usageModelRepository);
			simulatedUsers.add(user);
		}
		return simulatedUsers;
	}

	public SimulationMonitoring monitorSimulation() {
		return new SimulationStatus(simulatedUsers);
	}

}
