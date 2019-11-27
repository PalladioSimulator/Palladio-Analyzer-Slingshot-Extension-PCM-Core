package org.palladiosimulator.analyzer.slingshot.repositories.impl;

import static org.junit.Assert.assertEquals;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.palladiosimulator.analyzer.slingshot.helper.TestHelperConstants;
import org.palladiosimulator.analyzer.slingshot.helper.UsageModelTestHelper;
import org.palladiosimulator.pcm.usagemodel.UsageModel;
import org.palladiosimulator.pcm.usagemodel.UsageScenario;
import org.palladiosimulator.pcm.usagemodel.Workload;

public class UsageModelRepositoryImplTest {
	
	private UsageModelRepositoryImpl repository;

	@Before
	public void setUp() {
		repository = new UsageModelRepositoryImpl();
	}
	

	@Test
	public void testFindAllUsageScenarios() {
		Path path = Paths.get(TestHelperConstants.TEST_MODEL_BASE_PATH + "twoUsageScenariosClosedWorkload.usagemodel");
		UsageModel usageModel = UsageModelTestHelper.createUsageModelFromFile(path);
		repository.load(usageModel);
		List<UsageScenario> scenarios = repository.findAllUsageScenarios();
		assertEquals(scenarios.size(), 2);
	}
	
	
	@Test
	public void testFindWorkLoadForUsageScenario() {
		Path path = Paths.get(TestHelperConstants.TEST_MODEL_BASE_PATH + "closedWorkloadWithDelay.usagemodel");
		UsageModel usageModel = UsageModelTestHelper.createUsageModelFromFile(path);
		repository.load(usageModel);
		List<UsageScenario> scenarios = repository.findAllUsageScenarios();
		Workload workload = repository.findWorkloadForUsageScenario(scenarios.get(0));
		assertEquals(workload.getUsageScenario_Workload().getEntityName(), "UsageScenario");
	}
	
}
