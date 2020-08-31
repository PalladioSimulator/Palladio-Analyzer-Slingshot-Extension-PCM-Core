package org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl;

import static org.junit.Assert.assertEquals;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.palladiosimulator.analyzer.slingshot.helper.TestHelperConstants;
import org.palladiosimulator.analyzer.slingshot.helper.UsageModelTestHelper;
import org.palladiosimulator.analyzer.slingshot.repositories.UsageModelRepository;
import org.palladiosimulator.analyzer.slingshot.repositories.impl.UsageModelRepositoryImpl;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.entities.User;
import org.palladiosimulator.pcm.usagemodel.ClosedWorkload;
import org.palladiosimulator.pcm.usagemodel.UsageModel;

public class SimulatedUserProviderTest {

	private SimulatedUserProvider provider;
	private UsageModelRepository usageModelRepository;
	private UsageModel usageModel;

	@Before
	public void setUp() throws Exception {
		final Path testModelPath = Paths.get(TestHelperConstants.TEST_MODEL_BASE_PATH + "closedWorkloadWithDelay.usagemodel");
		usageModel = UsageModelTestHelper.createUsageModelFromFile(testModelPath);
		
		usageModelRepository = new UsageModelRepositoryImpl();
		usageModelRepository.load(usageModel);
		provider = new SimulatedUserProvider();
	}

	@Test
	public void testCreateSingleClosedWorkloadSimulatedUserForScenario() {
		provider.initializeRepository(usageModelRepository);
		final List<User> simulatedUsers = provider.createSimulatedUsers();
		assertEquals("Failed to create simulated user for scenario", simulatedUsers.size(), 1);
	}

	@Test
	public void createMultipleClosedWorkloadSimulatedUsersForScenario() {
		provider.initializeRepository(usageModelRepository);
		// set population to 2
		final int population = 2;
		final ClosedWorkload workload = (ClosedWorkload) usageModel.getUsageScenario_UsageModel().get(0).getWorkload_UsageScenario();
		workload.setPopulation(population);
		final List<User> simulatedUsers = provider.createSimulatedUsers();
		
		assertEquals("Failed to create simulated user for scenario", simulatedUsers.size(), 2);
	}
	
}
