package org.palladiosimulator.analyzer.slingshot.behavior.usagemodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.User;
import org.palladiosimulator.analyzer.slingshot.repositories.UsageModelRepository;
import org.palladiosimulator.pcm.usagemodel.ClosedWorkload;
import org.palladiosimulator.pcm.usagemodel.OpenWorkload;
import org.palladiosimulator.pcm.usagemodel.UsageScenario;
import org.palladiosimulator.pcm.usagemodel.Workload;

public class SimulatedUserProvider {
	
	private final Logger LOGGER = Logger.getLogger(SimulatedUserProvider.class);
	
	private UsageModelRepository usageModelRepository;
	
	public void initializeRepository(final UsageModelRepository usageModelRepository) {
		this.usageModelRepository = usageModelRepository;
	}
	
	public List<User> createSimulatedUsers() {
		if (usageModelRepository == null) {
			LOGGER.error("UsageModelRepository was not initialized. No usage model available to create simulated users.");
			return Collections.emptyList();
		}
		
		final List<UsageScenario> usageScenarios = usageModelRepository.findAllUsageScenarios();
	
		final List<User> simulatedUsers = new ArrayList<User>();
		
		for (final UsageScenario usageScenario : usageScenarios) {
			final Workload workload = usageModelRepository.findWorkloadForUsageScenario(usageScenario);
			
			if (workload instanceof ClosedWorkload) {
				LOGGER.info("Found closed workload");
				final ClosedWorkload closedWorkload = (ClosedWorkload) workload;
				// TODO ClosedWorkload simulation
			} else if (workload instanceof OpenWorkload) {
				LOGGER.info("Found open workload");
				// TODO: Open workload simulation currently not implemented.
			} else {
				LOGGER.info("Found undefined worload");
			}
		}
		
		return simulatedUsers;
	}
	
//	private List<User> createUsersForClosedWorkload(final UsageScenario scenario, final int population) {
//	List<User> simulatedUsers = new ArrayList<User>();
//	for (int i = 0; i < population; i++) {
//		AbstractUserAction currentPosition = usageModelRepository.findFirstActionOf(scenario);
//		User user = new User(currentPosition.getEntityName(), scenario, currentPosition, usageModelRepository);
//		simulatedUsers.add(user);
//	}
//	return simulatedUsers;
//}
}
