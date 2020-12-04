package org.palladiosimulator.analyzer.slingshot.simulation.core;

import java.util.List;
import java.util.function.Consumer;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.palladiosimulator.analyzer.slingshot.module.models.ModelModule;
import org.palladiosimulator.analyzer.slingshot.simulation.api.Simulation;
import org.palladiosimulator.analyzer.slingshot.simulation.core.events.SampleEventA;
import org.palladiosimulator.analyzer.slingshot.simulation.core.events.SampleEventB;
import org.palladiosimulator.analyzer.slingshot.simulation.core.events.SampleEventC;
import org.palladiosimulator.analyzer.slingshot.simulation.core.events.SimulationStarted;
import org.palladiosimulator.analyzer.slingshot.simulation.engine.SimulationEngine;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.SimulationBehaviorExtension;
import org.palladiosimulator.analyzer.slingshot.util.extensionpoint.ExtensionInstancesContainer;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provider;

public class SimulationBehaviorExtensionTest {

	private Simulation simulation;
	private SimulationEngine engine;

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
		simulation = new SimulationDriver(engine, new SimpleContainer());

		final ModelModule modelModule = new ModelModule();

		modelModule.getModelContainer().addModule(new LambdaModule(actualEventClazzes -> {
			final List<Class<?>> expectedEvents = List.of(SimulationStarted.class, SampleEventA.class,
			        SampleEventB.class, SampleEventC.class);

			LOGGER.info("called the callback");
			Assert.assertEquals(expectedEvents, actualEventClazzes);
		}));

		simulation.init(modelModule);
		simulation.startSimulation();
	}

	class LambdaModule extends AbstractModule implements Provider<CallbackInLastEvent> {

		private final CallbackInLastEvent lambda;

		public LambdaModule(final CallbackInLastEvent lambda) {
			this.lambda = lambda;
		}

		@Override
		protected void configure() {
			bind(CallbackInLastEvent.class).toProvider(this);
		}

		@Override
		public CallbackInLastEvent get() {
			return this.lambda;
		}
	}

	public interface CallbackInLastEvent extends Consumer<List<Class<?>>> {
	}

	class SimpleContainer implements ExtensionInstancesContainer<SimulationBehaviorExtension> {

		private Injector injector;

		@Override
		public void loadExtensions(final Injector injector) {
			this.injector = injector;
		}

		@Override
		public List<SimulationBehaviorExtension> getExtensions() {
			return List.of(injector.getInstance(SimulationBehaviorExtensionMock.class));
		}

	}
}
