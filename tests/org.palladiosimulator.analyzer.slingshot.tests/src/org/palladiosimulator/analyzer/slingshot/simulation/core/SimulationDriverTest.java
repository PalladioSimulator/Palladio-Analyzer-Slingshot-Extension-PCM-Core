package org.palladiosimulator.analyzer.slingshot.simulation.core;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.palladiosimulator.analyzer.slingshot.helper.TestHelperConstants;
import org.palladiosimulator.analyzer.slingshot.module.models.ModelModule;
import org.palladiosimulator.analyzer.slingshot.simulation.engine.SimulationEngine;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.SimulationBehaviorExtension;

import com.google.common.eventbus.EventBus;

@RunWith(MockitoJUnitRunner.class)
public class SimulationDriverTest {

	private static final Path testModelPath = Paths
	        .get(TestHelperConstants.TEST_MODEL_BASE_PATH + "closedWorkloadWithDelay.usagemodel");

	private SimulationDriver driver;

	private SimulationEngine simEngine;

	private EventBus simEngineEventBus;

	@Mock
	private SimulationBehaviorExtension simulationBehaviorExtension;
	@Mock
	private SimulationBehaviorExtension simulationBehaviorExtensionB;

	/**
	 * This injector contains simple modules just for testing purposes.
	 */
	private ModelModule modelModule;

	@BeforeClass
	public void setUpInjector() {
		modelModule = ModelModule.getInstance();
	}

	@Before
	public void setUp() {
		// core
		simEngine = new SimulationEngineMock();
		simEngineEventBus = simEngine.getEventDispatcher();

		// currently the tests consider no extensions (This is also just a unit test,
		// and not integration)
	}

//	@Test
//	public void testInitializeSingleBehaviorExtensionWorks() throws Exception {
//		final List<DecoratedSimulationBehaviorProvider> decoratedSimulationBehaviorProviders = new ArrayList<DecoratedSimulationBehaviorProvider>();
//		decoratedSimulationBehaviorProviders.add(simulationBehaviorExtensionProviderA);
//
//		driver = new SimulationDriver(simEngine, decoratedSimulationBehaviorProviders);
//
//		final UsageModel usageModel = UsageModelTestHelper.createUsageModelFromFile(testModelPath);
//
//		when(simulationBehaviorExtensionProviderA.decorateSimulationBehaviorWithInterceptors(any(), modelModule))
//		        .thenReturn(simulationBehaviorExtension);
//
//	}
//
//	@Test
//	public void testInitializeMultipleBehaviorExtensionsWorks() throws Exception {
//		final List<DecoratedSimulationBehaviorProvider> decoratedSimulationBehaviorProviders = new ArrayList<DecoratedSimulationBehaviorProvider>();
//		decoratedSimulationBehaviorProviders.add(simulationBehaviorExtensionProviderA);
//		decoratedSimulationBehaviorProviders.add(simulationBehaviorExtensionProviderB);
//
//		driver = new SimulationDriver(simEngine, decoratedSimulationBehaviorProviders);
//
//		final UsageModel usageModel = UsageModelTestHelper.createUsageModelFromFile(testModelPath);
//
//		when(simulationBehaviorExtensionProviderA.decorateSimulationBehaviorWithInterceptors(any(), modelModule))
//		        .thenReturn(simulationBehaviorExtension);
//		when(simulationBehaviorExtensionProviderB.decorateSimulationBehaviorWithInterceptors(any(), modelModule))
//		        .thenReturn(simulationBehaviorExtensionB);
//
//	}

}
