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
import org.palladiosimulator.analyzer.slingshot.simulation.engine.SimulationEngine;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.SimulationBehaviorExtension;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.decorators.DecoratedSimulationBehaviorProvider;
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

	@Test
	public void testInitializeSingleBehaviorExtensionWorks() throws Exception {
		final List<DecoratedSimulationBehaviorProvider> decoratedSimulationBehaviorProviders = new ArrayList<DecoratedSimulationBehaviorProvider>();
		decoratedSimulationBehaviorProviders.add(simulationBehaviorExtensionProviderA);

		driver = new SimulationDriver(simEngine, decoratedSimulationBehaviorProviders);

		final UsageModel usageModel = UsageModelTestHelper.createUsageModelFromFile(testModelPath);

		when(simulationBehaviorExtensionProviderA.decorateSimulationBehaviorWithInterceptors(any()))
				.thenReturn(simulationBehaviorExtension);

	}

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

	}

}
