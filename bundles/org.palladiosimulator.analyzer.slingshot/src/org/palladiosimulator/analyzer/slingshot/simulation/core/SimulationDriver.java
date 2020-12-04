package org.palladiosimulator.analyzer.slingshot.simulation.core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.module.models.ModelModule;
import org.palladiosimulator.analyzer.slingshot.simulation.api.Simulation;
import org.palladiosimulator.analyzer.slingshot.simulation.core.events.SimulationFinished;
import org.palladiosimulator.analyzer.slingshot.simulation.core.events.SimulationInterrupted;
import org.palladiosimulator.analyzer.slingshot.simulation.core.events.SimulationStarted;
import org.palladiosimulator.analyzer.slingshot.simulation.core.exceptions.EventContractException;
import org.palladiosimulator.analyzer.slingshot.simulation.engine.SimulationEngine;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.events.EventPrettyLogPrinter;
import org.palladiosimulator.analyzer.slingshot.simulation.events.graph.DefaultEventGraph;
import org.palladiosimulator.analyzer.slingshot.simulation.events.graph.EventGraph;
import org.palladiosimulator.analyzer.slingshot.simulation.events.graph.exportation.DotFileGraphExporter;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.BehaviorContainer;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.SimulationBehaviorExtension;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.EventContract;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.EventMethod;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.interceptors.EventMonitoringInterceptor;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.interceptors.ExtensionLoggingInterceptor;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.interceptors.ExtensionMethodHandlerWithInterceptors;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.interceptors.Interceptor;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.interceptors.SchedulingInterceptor;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.interceptors.SimulationExtensionOnEventContractEnforcementInterceptor;
import org.palladiosimulator.analyzer.slingshot.util.extensionpoint.ExtensionInstancesContainer;

import com.google.common.base.Preconditions;
import com.google.inject.AbstractModule;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matchers;

/**
 * The SimulationDriver simulates and executes the registered behaviors and its
 * interceptors. It holds a list of {@link DecoratedSimulationBehaviorProvider}
 * and their corresponding {@link SimulationBehavioExtension} and puts them into
 * the eventBus. The EventBus is provided by a {@link SimulationEngine}.
 * <p>
 * When the simulation starts, it will always fire a {@link SimulationStarted}
 * event at the beginning. Afterwards, all the event listeners to this event
 * will be called. When no event is called again, then {@link SimulationFinshed}
 * event will be called one last time so that extensions can clear things up.
 * 
 * 
 * @author Julijan Katic
 */
public class SimulationDriver implements Simulation, SimulationScheduling {

	private static final Logger LOGGER = Logger.getLogger(SimulationDriver.class);

	/**
	 * The simulation engine that is responsible for dispatching events.
	 */
	private final SimulationEngine simEngine;

	/**
	 * The instance container of behavior extensions;
	 */
	private final ExtensionInstancesContainer<SimulationBehaviorExtension> simulationBehaviorExtensions;

	/**
	 * Map of event counters to see which event has to be processed.
	 */
	private final Map<DESEvent, Integer> eventCounter = new HashMap<>();

	/**
	 * Specifies whether the simulation has started.
	 */
	private boolean simulationStarted = false;

	/**
	 * The event graph monitoring all events.
	 */
	private final EventGraph eventGraph = new DefaultEventGraph();

	/**
	 * Convenient constructor that automatically loads all the extensions.
	 * 
	 * @param simEngine The non-null simulation engine.
	 */
	public SimulationDriver(final SimulationEngine simEngine) {
		this(simEngine, new BehaviorContainer());
	}

	/**
	 * Instantiates the driver with an engine and the container of extensions. This
	 * will also add an Event Exception Handler.
	 * 
	 * @param simEngine           The non-null simulation engine.
	 * @param extensionsContainer The non-null container of
	 *                            SimulationBehaviorExtension.
	 */
	public SimulationDriver(final SimulationEngine simEngine,
	        final ExtensionInstancesContainer<SimulationBehaviorExtension> extensionsContainer) {
		Preconditions.checkNotNull(simEngine);
		Preconditions.checkNotNull(extensionsContainer);
		this.simEngine = simEngine;
		this.simulationBehaviorExtensions = extensionsContainer;
		this.simEngine.loadEventExceptionHandler((exception, event) -> {
			LOGGER.error("An error occured while processing the event " + event.getId() + ".", exception);
			if (!this.simulationStarted) {
				this.simulationStarted = false;
				return new SimulationInterrupted(exception);
			} else {
				return null;
			}
		});
	}

	@Override
	public void init(final ModelModule modelInjector) throws Exception {
		LOGGER.info("Start simulation driver initialization using model injector.");

		registerInterceptorInModelModule(modelInjector);

		simulationBehaviorExtensions.loadExtensions(modelInjector.getInjector());

		/* Register every extension behavior to the dispatcher */
		simulationBehaviorExtensions.getExtensions().stream().forEach(extension -> {
			extension.init();
			this.simEngine.registerEventListener(extension);
			LOGGER.info(
			        "Registered behavior extension to the event dispatcher: " + extension.getClass().getSimpleName());
		});

		LOGGER.info("Finished simulation driver initialization");
	}

	private void registerInterceptorInModelModule(final ModelModule modelModule) {
		modelModule.getModelContainer().addModule(new InterceptorModule(this, this.getEventGraph()));
	}

	@Override
	public void startSimulation() {
		this.simulationStarted = true;
		final DESEvent simulationStart = new SimulationStarted();

		simEngine.init();

		simEngine.scheduleEvent(simulationStart);

		LOGGER.debug("Start simulation");
		simEngine.start();

		this.scheduleForSimulation(new SimulationFinished());
		this.exportGraph();
	}

	@Override
	public void stopSimulation() {
		this.simulationStarted = false;
		simEngine.scheduleEvent(new SimulationInterrupted("Called interruption."));
		this.exportGraph();
	}

	/**
	 * Convenience method that exports the event graph after the simulation has
	 * stopped.
	 */
	private void exportGraph() {
		try {
			final String fileName = "EventGraph.dot";
			final File file = new File(fileName);
			final FileWriter fileWriter = new FileWriter(file);
			final DotFileGraphExporter exporter = new DotFileGraphExporter(fileWriter);

			LOGGER.info("Export Graph to: " + fileName);

			this.eventGraph.exportGraph(exporter);
		} catch (final IOException e) {
			LOGGER.error("Couldn't export event graph due to exception.", e);
		}
	}

	@Override
	public void scheduleForSimulation(final DESEvent evt) {
		if (!simulationStarted) {
			LOGGER.debug("Couldn't publish event: " + evt.getId());
			return;
		}

		LOGGER.info(EventPrettyLogPrinter.prettyPrint(evt, "Scheduled for simulation", "Simulation Driver"));

		try {
			checkEventContract(evt);
			simEngine.scheduleEvent(evt);
		} catch (final EventContractException e) {
			LOGGER.error("Couldn't publish event.", e);
		}
	}

	@Override
	public void scheduleForSimulation(final List<DESEvent> evt) {
		if (!simulationStarted) {
			LOGGER.debug("Cannot publish events as the simulation has stopped.");
			return;
		}

		for (final DESEvent desEvent : evt) {
			scheduleForSimulation(desEvent);
		}
	}

	@Override
	public void checkEventContract(final DESEvent event) throws EventContractException {
		final EventContract contract = event.getClass().getAnnotation(EventContract.class);

		if (contract != null) {
			final int numberOfSpawns = this.eventCounter.getOrDefault(event, 0);

			if (contract.maximalPublishing() <= numberOfSpawns) {
				throw new EventContractException(contract, event, "Maximum number of publishments reached.");
			}

			this.eventCounter.put(event, numberOfSpawns + 1);
		}
	}

	public EventGraph getEventGraph() {
		return eventGraph;
	}

	/**
	 * A module that defines the interceptors.
	 */
	class InterceptorModule extends AbstractModule {

		private final List<Interceptor> interceptors;

		public InterceptorModule(final SimulationScheduling scheduler, final EventGraph eventGraph) {
			final ExtensionLoggingInterceptor loggingInterceptor = new ExtensionLoggingInterceptor();
			final SchedulingInterceptor schedulingInterceptor = new SchedulingInterceptor(scheduler);
			final SimulationExtensionOnEventContractEnforcementInterceptor contractInterceptor = new SimulationExtensionOnEventContractEnforcementInterceptor();
			final EventMonitoringInterceptor eventMonitoringInterceptor = new EventMonitoringInterceptor(eventGraph);

			interceptors = List.of(loggingInterceptor, schedulingInterceptor, contractInterceptor, eventMonitoringInterceptor);
		}

		@Override
		protected void configure() {
			bindInterceptor(
			        Matchers.any(),
			        new ExtensionMethodMatcher(),
			        new ExtensionMethodHandlerWithInterceptors(interceptors));
		}

	}

	/**
	 * Matcher that returns true if the method either starts with "on" or is
	 * annotated with {@link EventMethod} Annotation.
	 */
	class ExtensionMethodMatcher extends AbstractMatcher<Method> implements Serializable {

		private static final long serialVersionUID = 1L;

		@Override
		public boolean matches(final Method method) {
			final boolean isTheRightMethod = method.getName().startsWith("on")
			        || method.getAnnotation(EventMethod.class) != null;
			LOGGER.debug(String.format("Method name: %s (%s)", method.getName(), isTheRightMethod));
			return isTheRightMethod;
		}

	}

}
