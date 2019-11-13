package org.palladiosimulator.analyzer.slingshot.repositories.impl;

import static org.junit.Assert.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.palladiosimulator.analyzer.slingshot.helper.UsageModelTestHelper;
import org.palladiosimulator.pcm.usagemodel.UsageModel;
import org.palladiosimulator.pcm.usagemodel.UsageScenario;
import org.palladiosimulator.pcm.usagemodel.Workload;

public class UsageModelRepositoryImplTest {


	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testFindAllUsageScenarios() {
		Path path = Paths.get("C:\\dev\\repos\\git\\Mosaic\\palladio-analyzer-slingshot\\tests\\org.palladiosimulator.analyzer.slingshot.tests\\model\\twoUsageScenariosClosedWorkload.usagemodel");
		UsageModel usageModel = UsageModelTestHelper.createUsageModelFromFile(path);
		UsageModelRepositoryImpl repository = new UsageModelRepositoryImpl(usageModel);
		List<UsageScenario> scenarios = repository.findAllUsageScenarios();
		assertEquals(scenarios.size(), 2);
	}
	
	
	@Test
	public void testFindWorkLoadForUsageScenario() {
		Path path = Paths.get("C:\\dev\\repos\\git\\Mosaic\\palladio-analyzer-slingshot\\tests\\org.palladiosimulator.analyzer.slingshot.tests\\model\\closedWorkloadWithDelay.usagemodel");
		UsageModel usageModel = UsageModelTestHelper.createUsageModelFromFile(path);
		UsageModelRepositoryImpl repository = new UsageModelRepositoryImpl(usageModel);
		List<UsageScenario> scenarios = repository.findAllUsageScenarios();
		Workload workload = repository.findWorkloadForUsageScenario(scenarios.get(0));
		assertEquals(workload.getUsageScenario_Workload().getEntityName(), "UsageScenario");
	}

	
}
