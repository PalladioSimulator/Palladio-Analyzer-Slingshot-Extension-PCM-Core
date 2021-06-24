package org.palladiosimulator.analyzer.slingshot.simulation.core;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.MockitoRule;
import org.palladiosimulator.analyzer.slingshot.helper.TestHelperConstants;
import org.palladiosimulator.analyzer.slingshot.simulation.engine.SimulationEngine;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.SimulationBehaviorExtension;

@RunWith(MockitoJUnitRunner.class)
public class SimulationDriverTest {

	private static final Path testModelPath = Paths
			.get(TestHelperConstants.TEST_MODEL_BASE_PATH + "closedWorkloadWithDelay.usagemodel");

	private SimulationDriver driver;

	@Mock
	private SimulationEngine simEngine;

	@Mock
	private SimulationBehaviorExtension simulationBehaviorExtension;
	@Mock
	private SimulationBehaviorExtension simulationBehaviorExtensionB;

	@Rule
	private final MockitoRule mockitoRule = MockitoJUnit.rule();

	/**
	 * This injector contains simple modules just for testing purposes.
	 */
	// private ModelModule modelModule;

	@BeforeClass
	public void setUpInjector() {
		// this.modelModule = new ModelModule();
	}

	@Before
	public void setUp() {
//		simEngine = new SimulationEngineMock();
	}

	@Test
	public void testSimulationDriverInitialization() throws Exception {
		final SimulationDriver simulationDriver = new SimulationDriver(this.simEngine, null);

		// simulationDriver.init(this.modelModule);
	}
}
