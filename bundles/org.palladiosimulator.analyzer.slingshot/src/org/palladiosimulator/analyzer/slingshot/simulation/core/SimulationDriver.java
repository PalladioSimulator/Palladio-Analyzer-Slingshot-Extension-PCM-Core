package org.palladiosimulator.analyzer.slingshot.simulation.core;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.module.models.ModelModule;
import org.palladiosimulator.analyzer.slingshot.simulation.api.Simulation;
import org.palladiosimulator.analyzer.slingshot.simulation.core.events.SimulationStarted;
import org.palladiosimulator.analyzer.slingshot.simulation.engine.SimulationEngine;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.events.EventPrettyLogPrinter;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.BehaviorContainer;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.SimulationBehaviorExtension;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.EventMethod;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.interceptors.ExtensionLoggingInterceptor;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.interceptors.ExtensionMethodHandlerWithInterceptors;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.interceptors.Interceptor;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.interceptors.SchedulingInterceptor;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.interceptors.SimulationExtensionOnEventContractEnforcementInterceptor;
import org.palladiosimulator.analyzer.slingshot.util.extensionpoint.ExtensionInstancesContainer;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matchers;

/**
 * The SimulationDriver simulates and executes the registered behaviors and its
 * interceptors. It holds a list of {@link DecoratedSimulationBehaviorProvider}
 * and their corresponding {@link SimulationBehavioExtension} and puts them into
 * the eventBus. The EventBus is provided by a {@link SimulationEngine}.
 * 
 * @author Julijan Katic
 */
public class SimulationDriver implements Simulation, SimulationScheduling {

	private final Logger LOGGER = Logger.getLogger(SimulationDriver.class);

	/**
	 * The simulation engine that is responsible for dispatching events.
	 */
	private final /*@ spec_public @*/ SimulationEngine simEngine;

	/**
	 * The list of simulation behavior extensions. These will be initialized later
	 * by the {@link DecoratedSimulationBehaviorProvider}s.
	 */
	private final /*@ spec_public @*/ List<SimulationBehaviorExtension> behaviorExtensions;

	private final /*@ spec_public @*/ List<Interceptor> interceptors;

	private final ExtensionInstancesContainer<SimulationBehaviorExtension> simulationBehaviorExtensions;

	public SimulationDriver(final SimulationEngine simEngine) {
		this(simEngine, BehaviorContainer.getInstance());
	}

	public SimulationDriver(final SimulationEngine simEngine,
	        final ExtensionInstancesContainer<SimulationBehaviorExtension> extensionsContainer) {
		this.simEngine = simEngine;
		this.behaviorExtensions = new ArrayList<>();
		this.simulationBehaviorExtensions = extensionsContainer;
		this.interceptors = new ArrayList<>();
	}

	@Override
	public void init(final ModelModule modelInjector) throws Exception {
		LOGGER.info("Start simulation driver initialization using model injector.");

		initializeInterceptors();
		registerInterceptorInModelModule(modelInjector);

		simulationBehaviorExtensions.loadExtensions(modelInjector.getInjector());

		simulationBehaviorExtensions.getExtensions().stream().forEach(extension -> {
			extension.init();
			this.simEngine.getEventDispatcher().register(extension);
			LOGGER.info(
			        "Registered behavior extension to the event dispatcher: " + extension.getClass().getSimpleName());
		});

		this.simEngine.getEventDispatcher().register(this);

		LOGGER.info("Finished simulation driver initialization");
	}

	/**
	 * Initializes the interceptor list and puts the
	 * {@link ExtensionLoggingInterceptor}, {@link SchedulingIntercepor} and
	 * {@link SimulationExtensionOnEventContractEnforcementInterceptor} in it.
	 */
	private /* @ helper @ */ void initializeInterceptors() {
		final ExtensionLoggingInterceptor loggingInterceptor = new ExtensionLoggingInterceptor();
		final SchedulingInterceptor schedulingInterceptor = new SchedulingInterceptor(this);
		final SimulationExtensionOnEventContractEnforcementInterceptor contractInterceptor = new SimulationExtensionOnEventContractEnforcementInterceptor();

		interceptors.addAll(List.of(loggingInterceptor, schedulingInterceptor, contractInterceptor));
	}

	private void registerInterceptorInModelModule(final ModelModule modelModule) {
		modelModule.getModelContainer().addModule(new InterceptorModule());
	}

	@Override
	public void startSimulation() {
		final DESEvent simulationStart = new SimulationStarted();

		simEngine.init();

		simEngine.scheduleEvent(simulationStart);

		LOGGER.debug("Start simulation");
		simEngine.start();
	}

	/**
	 * @return
	 */
	public SimulationMonitoring monitorSimulation() {
		return new SimulationStatus(null);
	}

	@Override
	public void scheduleForSimulation(final DESEvent evt) {
		LOGGER.info(EventPrettyLogPrinter.prettyPrint(evt, "Scheduled for simulation", "Simulation Driver"));
		simEngine.scheduleEvent(evt);
	}

	@Override
	public void scheduleForSimulation(final List<DESEvent> evt) {
		for (final DESEvent desEvent : evt) {
			scheduleForSimulation(desEvent);
		}

	}

	class InterceptorModule extends AbstractModule {

		@Override
		protected void configure() {
			bindInterceptor(
			        Matchers.any(),
			        // Matchers.any(),
			        new ExtensionMethodMatcher().or(Matchers.annotatedWith(EventMethod.class)),
			        new ExtensionMethodHandlerWithInterceptors(interceptors));
		}

	}

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
