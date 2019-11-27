package org.palladiosimulator.analyzer.slingshot.simulation.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.palladiosimulator.analyzer.slingshot.helper.TestHelperConstants;
import org.palladiosimulator.analyzer.slingshot.helper.UsageModelTestHelper;
import org.palladiosimulator.analyzer.slingshot.repositories.UsageModelRepository;
import org.palladiosimulator.analyzer.slingshot.repositories.impl.UsageModelRepositoryImpl;
import org.palladiosimulator.analyzer.slingshot.simulation.core.SimulationDriver;
import org.palladiosimulator.analyzer.slingshot.simulation.core.SimulationMonitoring;
import org.palladiosimulator.analyzer.slingshot.simulation.engine.SimulationEngine;
import org.palladiosimulator.analyzer.slingshot.simulation.engine.SimulationEngineMock;
import org.palladiosimulator.analyzer.slingshot.simulation.events.Dispatcher;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.SimulatedUserProvider;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.UsageSimulation;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.UsageSimulationImpl;
import org.palladiosimulator.pcm.usagemodel.UsageModel;
import org.palladiosimulator.pcm.usagemodel.UsageScenario;


public class SimulationDriverTest {
	
	private static final Path testModelPath = Paths.get(TestHelperConstants.TEST_MODEL_BASE_PATH + "closedWorkloadWithDelay.usagemodel");
	
	private SimulationDriver driver;
	
	private SimulationEngine simEngine;
	private SimulatedUserProvider simulatedUsersProvider;
	private UsageModelRepository usageModelRepository;

	private SimulationBehaviourExtension usageSimulation;

	private Dispatcher eventDispatcher;
	
	@Before
	public void setUp() {
		// extension
		usageModelRepository = new UsageModelRepositoryImpl();
		
		simulatedUsersProvider = new SimulatedUserProvider();
		usageSimulation = new UsageSimulationImpl(usageModelRepository, simulatedUsersProvider);
		
		// core
		eventDispatcher = new Dispatcher();
		simEngine = new SimulationEngineMock(eventDispatcher);
		
		var simulationBehaviorExtensions = new ArrayList<SimulationBehaviourExtension>();
		simulationBehaviorExtensions.add(usageSimulation);
		driver = new SimulationDriver(simEngine,simulationBehaviorExtensions);
	}

	
	//FIXME:: How to rewrite this test. What is SimulationMonitoring? This is already now an integration test isn't it?
	@Ignore
	public void testInitializeClosedWorkloadSimulationforSingleUser() {
		UsageModel usageModel = UsageModelTestHelper.createUsageModelFromFile(testModelPath);
		
		driver.init(usageModel);
		
		SimulationMonitoring simulationStatus = driver.monitorSimulation();
		assertEquals("Failed to initialize closed workload simulation for single user", simulationStatus.getSimulatedUsers().size(), 1);
		
		UsageScenario expectedScenario = usageModel.getUsageScenario_UsageModel().get(0);
		assertEquals("Failed to initialize simulated scenario for single user", simulationStatus.getSimulatedUsers().get(0).currentScenario(), expectedScenario);
		assertEquals("Failed to initialize start position within scenario for single user", simulationStatus.getSimulatedUsers().get(0).currentPosition()
				, expectedScenario.getScenarioBehaviour_UsageScenario().getActions_ScenarioBehaviour().get(0));
		assertTrue("Failed to schedule initial startEvent for single user", simEngine.hasScheduledEvents());
	}

}
