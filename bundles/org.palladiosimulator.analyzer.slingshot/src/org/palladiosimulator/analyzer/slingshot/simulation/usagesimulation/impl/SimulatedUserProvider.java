package org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.repositories.UsageModelRepository;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.entities.User;
import org.palladiosimulator.pcm.usagemodel.AbstractUserAction;
import org.palladiosimulator.pcm.usagemodel.ClosedWorkload;
import org.palladiosimulator.pcm.usagemodel.OpenWorkload;
import org.palladiosimulator.pcm.usagemodel.UsageScenario;
import org.palladiosimulator.pcm.usagemodel.Workload;


public class SimulatedUserProvider {
	
	
	private final Logger LOGGER = Logger.getLogger(SimulatedUserProvider.class);
	
	private UsageModelRepository usageModelRepository;
	
	
	public void initializeRepository(UsageModelRepository usageModelRepository) {
		this.usageModelRepository = usageModelRepository;
	}
	
	public List<User> createSimulatedUsers() {
		if (usageModelRepository == null) {
			LOGGER.error("UsageModelRepository was not initialized. No usage model available to create simulated users");
			return new ArrayList<User>();
		}
		
		// parse usage model
		List<UsageScenario> usageScenarios = usageModelRepository.findAllUsageScenarios();
		
		List<User> simulatedUsers = new ArrayList<User>();
		for (UsageScenario usageScenario : usageScenarios) {
			Workload workload = usageModelRepository.findWorkloadForUsageScenario(usageScenario);
			
			if (workload instanceof ClosedWorkload ) {
				LOGGER.info("Found closed workload");
				ClosedWorkload closedWorkload = (ClosedWorkload) workload;
				int population = closedWorkload.getPopulation();
//				simulatedUsers.addAll(createUsersForClosedWorkload(usageScenario, population));

			} else if (workload instanceof OpenWorkload) {
				LOGGER.info("Found open workload");
				LOGGER.info("FIXME: Open workload simulation currently not implemented.");
				
			} else {
				LOGGER.info("Found undefined workload");
			}
			
		}
		
		return simulatedUsers;
	}
	
//	private List<User> createUsersForClosedWorkload(final UsageScenario scenario, final int population) {
//		List<User> simulatedUsers = new ArrayList<User>();
//		for (int i = 0; i < population; i++) {
//			AbstractUserAction currentPosition = usageModelRepository.findFirstActionOf(scenario);
//			User user = new User(currentPosition.getEntityName(), scenario, currentPosition, usageModelRepository);
//			simulatedUsers.add(user);
//		}
//		return simulatedUsers;
//	}

	
}
