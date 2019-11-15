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
import org.palladiosimulator.pcm.usagemodel.ClosedWorkload;
import org.palladiosimulator.pcm.usagemodel.UsageModel;

public class SimulatedUserProviderTest {

	private SimulatedUserProvider provider;
	private UsageModelRepository usageModelRepository;
	private UsageModel usageModel;

	@Before
	public void setUp() throws Exception {
		Path testModelPath = Paths.get(TestHelperConstants.TEST_MODEL_BASE_PATH + "closedWorkloadWithDelay.usagemodel");
		usageModel = UsageModelTestHelper.createUsageModelFromFile(testModelPath);
		
		usageModelRepository = new UsageModelRepositoryImpl(usageModel);
		provider = new SimulatedUserProvider(usageModelRepository);
	}

	@Test
	public void testCreateSingleClosedWorkloadSimulatedUserForScenario() {
		List<SimulatedUser> simulatedUsers = provider.createClosedWorkloadSimulatedUsers();
		assertEquals("Failed to create simulated user for scenario", simulatedUsers.size(), 1);
	}

	@Test
	public void createMultipleClosedWorkloadSimulatedUsersForScenario() {
		// set population to 2
		int population = 2;
		ClosedWorkload workload = (ClosedWorkload) usageModel.getUsageScenario_UsageModel().get(0).getWorkload_UsageScenario();
		workload.setPopulation(population);
		List<SimulatedUser> simulatedUsers = provider.createClosedWorkloadSimulatedUsers();
		
		assertEquals("Failed to create simulated user for scenario", simulatedUsers.size(), 2);
	}
	
}
