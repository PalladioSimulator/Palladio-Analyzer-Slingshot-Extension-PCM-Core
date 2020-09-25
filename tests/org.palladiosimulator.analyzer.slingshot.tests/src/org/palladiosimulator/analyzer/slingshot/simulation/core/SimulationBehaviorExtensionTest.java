package org.palladiosimulator.analyzer.slingshot.simulation.core;

import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.palladiosimulator.analyzer.slingshot.simulation.api.Simulation;
import org.palladiosimulator.analyzer.slingshot.simulation.core.events.SampleEventA;
import org.palladiosimulator.analyzer.slingshot.simulation.core.events.SampleEventB;
import org.palladiosimulator.analyzer.slingshot.simulation.core.events.SampleEventC;
import org.palladiosimulator.analyzer.slingshot.simulation.core.events.SimulationStarted;
import org.palladiosimulator.analyzer.slingshot.simulation.engine.SimulationEngine;

public class SimulationBehaviorExtensionTest {

	private Simulation simulation;
	private SimulationEngine engine;

	private SimulationBehaviorExtensionMock.Decorator mockProvider;

	private final Logger LOGGER = Logger.getLogger(SimulationBehaviorExtensionTest.class);

	@Before
	public void initTest() {
		/*
		 * Output log information to the real console.
		 */
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}

	@Test
	public void checkForTheEvents() throws Exception {
		engine = new SimulationEngineMock();
		mockProvider = new SimulationBehaviorExtensionMock.Decorator(actualEventClazzes -> {
			final List<Class<?>> expectedEvents = List.of(SimulationStarted.class, SampleEventA.class,
					SampleEventB.class, SampleEventC.class);

			LOGGER.info("called the callback");
			Assert.assertEquals(expectedEvents, actualEventClazzes);
		});

		simulation = new SimulationDriver(engine, List.of(mockProvider));
		simulation.init(null); // We don't need the model in this test.
		simulation.startSimulation();
	}
}
