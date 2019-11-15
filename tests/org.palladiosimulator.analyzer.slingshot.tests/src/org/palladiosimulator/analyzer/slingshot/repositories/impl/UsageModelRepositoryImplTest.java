package org.palladiosimulator.analyzer.slingshot.repositories.impl;

import static org.junit.Assert.assertEquals;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Test;
import org.palladiosimulator.analyzer.slingshot.helper.TestHelperConstants;
import org.palladiosimulator.analyzer.slingshot.helper.UsageModelTestHelper;
import org.palladiosimulator.pcm.usagemodel.UsageModel;
import org.palladiosimulator.pcm.usagemodel.UsageScenario;
import org.palladiosimulator.pcm.usagemodel.Workload;

public class UsageModelRepositoryImplTest {


	@Test
	public void testFindAllUsageScenarios() {
		Path path = Paths.get(TestHelperConstants.TEST_MODEL_BASE_PATH + "twoUsageScenariosClosedWorkload.usagemodel");
		UsageModel usageModel = UsageModelTestHelper.createUsageModelFromFile(path);
		UsageModelRepositoryImpl repository = new UsageModelRepositoryImpl(usageModel);
		List<UsageScenario> scenarios = repository.findAllUsageScenarios();
		assertEquals(scenarios.size(), 2);
	}
	
	
	@Test
	public void testFindWorkLoadForUsageScenario() {
		Path path = Paths.get(TestHelperConstants.TEST_MODEL_BASE_PATH + "closedWorkloadWithDelay.usagemodel");
		UsageModel usageModel = UsageModelTestHelper.createUsageModelFromFile(path);
		UsageModelRepositoryImpl repository = new UsageModelRepositoryImpl(usageModel);
		List<UsageScenario> scenarios = repository.findAllUsageScenarios();
		Workload workload = repository.findWorkloadForUsageScenario(scenarios.get(0));
		assertEquals(workload.getUsageScenario_Workload().getEntityName(), "UsageScenario");
	}
	
}
