package org.palladiosimulator.analyzer.slingshot.simulation.core;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.palladiosimulator.analyzer.slingshot.helper.TestHelperConstants;
import org.palladiosimulator.analyzer.slingshot.helper.UsageModelTestHelper;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.behavioral.SimulationBehaviorExtension;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.behavioral.decorators.DecoratedSimulationBehaviorProvider;
import org.palladiosimulator.analyzer.slingshot.simulation.engine.SimulationEngine;
import org.palladiosimulator.analyzer.slingshot.simulation.engine.SimulationEngineMock;
import org.palladiosimulator.pcm.usagemodel.UsageModel;

import com.google.common.eventbus.EventBus;

@RunWith(MockitoJUnitRunner.class)
public class SimulationDriverTest {

	private static final Path testModelPath = Paths
			.get(TestHelperConstants.TEST_MODEL_BASE_PATH + "closedWorkloadWithDelay.usagemodel");

	private SimulationDriver driver;

	private SimulationEngine simEngine;

	@Mock
	private DecoratedSimulationBehaviorProvider simulationBehaviorExtensionProviderA;
	@Mock
	private DecoratedSimulationBehaviorProvider simulationBehaviorExtensionProviderB;

	private EventBus simEngineEventBus;

	@Mock
	private SimulationBehaviorExtension simulationBehaviorExtension;
	@Mock
	private SimulationBehaviorExtension simulationBehaviorExtensionB;

	@Before
	public void setUp() {
		// core
		simEngine = new SimulationEngineMock();
		simEngineEventBus = simEngine.getEventDispatcher();

		// currently the tests consider no extensions
	}

	// FIXME:: How to rewrite this test. What is SimulationMonitoring? This is
	// already now an integration test isn't it?

	// asert that inceptor registration works
	// assert that dispatcher registration works (driver)
	// assert that dispatcher registration works for behaviorExtentiosn (single |
	// multiple)
	// if there is not behaviorExtension -> throw an exception ? -> invalid test
	// case; not relevant

	@Test
	public void testInitializeSingleBehaviorExtensionWorks() throws Exception {
		final List<DecoratedSimulationBehaviorProvider> decoratedSimulationBehaviorProviders = new ArrayList<DecoratedSimulationBehaviorProvider>();
		decoratedSimulationBehaviorProviders.add(simulationBehaviorExtensionProviderA);

		driver = new SimulationDriver(simEngine, decoratedSimulationBehaviorProviders);

		final UsageModel usageModel = UsageModelTestHelper.createUsageModelFromFile(testModelPath);

		when(simulationBehaviorExtensionProviderA.decorateSimulationBehaviorWithInterceptors(any()))
				.thenReturn(simulationBehaviorExtension);

//		driver.init(usageModel);

//		verify(simulationBehaviorExtension,times(1)).init(usageModel);
	}

	// intention: every init of each extension is called
	@Test
	public void testInitializeMultipleBehaviorExtensionsWorks() throws Exception {
		final List<DecoratedSimulationBehaviorProvider> decoratedSimulationBehaviorProviders = new ArrayList<DecoratedSimulationBehaviorProvider>();
		decoratedSimulationBehaviorProviders.add(simulationBehaviorExtensionProviderA);
		decoratedSimulationBehaviorProviders.add(simulationBehaviorExtensionProviderB);

		driver = new SimulationDriver(simEngine, decoratedSimulationBehaviorProviders);

		final UsageModel usageModel = UsageModelTestHelper.createUsageModelFromFile(testModelPath);

		when(simulationBehaviorExtensionProviderA.decorateSimulationBehaviorWithInterceptors(any()))
				.thenReturn(simulationBehaviorExtension);
		when(simulationBehaviorExtensionProviderB.decorateSimulationBehaviorWithInterceptors(any()))
				.thenReturn(simulationBehaviorExtensionB);

//		driver.init(usageModel);

//		verify(simulationBehaviorExtension,times(1)).init(usageModel);
//		verify(simulationBehaviorExtensionB,times(1)).init(usageModel);

	}

}
