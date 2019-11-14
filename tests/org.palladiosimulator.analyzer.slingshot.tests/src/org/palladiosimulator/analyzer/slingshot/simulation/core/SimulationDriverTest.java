package org.palladiosimulator.analyzer.slingshot.simulation.core;

import static org.junit.Assert.assertEquals;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;
import org.palladiosimulator.analyzer.slingshot.helper.TestHelperConstants;
import org.palladiosimulator.analyzer.slingshot.helper.UsageModelTestHelper;
import org.palladiosimulator.analyzer.slingshot.simulation.core.SimulationDriver;
import org.palladiosimulator.analyzer.slingshot.simulation.core.SimulationMonitoring;
import org.palladiosimulator.pcm.usagemodel.UsageModel;
import org.palladiosimulator.pcm.usagemodel.UsageScenario;


public class SimulationDriverTest {

	
	@Test
	public void testInitializeClosedWorkloadSimulationforSingleUser() {
		Path testModelPath = Paths.get(TestHelperConstants.TEST_MODEL_BASE_PATH + "closedWorkloadWithDelay.usagemodel");
		UsageModel usageModel = UsageModelTestHelper.createUsageModelFromFile(testModelPath);

		SimulationDriver driver = new SimulationDriver(usageModel);
		
		driver.init();
		
		SimulationMonitoring simulationStatus = driver.monitorSimulation();
		assertEquals("Failed to initialize closed workload simulation for single user", simulationStatus.getSimulatedUsers().size(), 1);
		
		UsageScenario expectedScenario = usageModel.getUsageScenario_UsageModel().get(0);
		assertEquals("Failed to initialize simulated scenario for single user", simulationStatus.getSimulatedUsers().get(0).currentScenario(), expectedScenario);
		assertEquals("Failed to initialize start position within scenario for single user", simulationStatus.getSimulatedUsers().get(0).currentPosition()
				, expectedScenario.getScenarioBehaviour_UsageScenario().getActions_ScenarioBehaviour().get(0));

		// start event is scheduled
	}

}
