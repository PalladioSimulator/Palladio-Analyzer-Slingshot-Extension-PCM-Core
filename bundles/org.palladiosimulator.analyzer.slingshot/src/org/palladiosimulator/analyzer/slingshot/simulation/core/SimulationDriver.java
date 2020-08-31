package org.palladiosimulator.analyzer.slingshot.simulation.core;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.simulation.api.Simulation;
import org.palladiosimulator.analyzer.slingshot.simulation.api.SimulationModel;
import org.palladiosimulator.analyzer.slingshot.simulation.core.events.SimulationStarted;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.behavioural.ExtensionLoggingInterceptor;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.behavioural.Interceptor;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.behavioural.SchedulingInterceptor;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.behavioural.SimulationBehaviourExtension;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.behavioural.SimulationExtensionOnEventContractEnforcementInterceptor;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.behavioural.decorators.DecoratedSimulationBehaviorProvider;
import org.palladiosimulator.analyzer.slingshot.simulation.engine.SimulationEngine;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.events.EventPrettyLogPrinter;

public class SimulationDriver implements Simulation, SimulationScheduling {

	private final Logger LOGGER = Logger.getLogger(SimulationDriver.class);

	// FIXME: Remove the dependency to usageSimulation.
	private final SimulationEngine simEngine;

	private final List<SimulationBehaviourExtension> behaviorExtensions;

	private List<DecoratedSimulationBehaviorProvider> decoratedSimulationBehaviorProviders;

	public SimulationDriver(final SimulationEngine simEngine) {
		this.simEngine = simEngine;
		this.behaviorExtensions = new ArrayList<SimulationBehaviourExtension>();
	}

	public SimulationDriver(final SimulationEngine simEngine, final List<DecoratedSimulationBehaviorProvider> decoratedSimProviders) {
		this.simEngine = simEngine;
		this.behaviorExtensions = new ArrayList<SimulationBehaviourExtension>();
		this.decoratedSimulationBehaviorProviders = new ArrayList<DecoratedSimulationBehaviorProvider>();
		this.decoratedSimulationBehaviorProviders.addAll(decoratedSimProviders);
	}

	
	@Override
	public void init(final SimulationModel model) throws Exception {
		LOGGER.info("Start simulation driver initialization.");

		registerSimulationBehaviorExtensionInterceptors();

		for (final SimulationBehaviourExtension simulationBehaviourExtension : behaviorExtensions) {
			simulationBehaviourExtension.init(model);
			this.simEngine.getEventDispatcher().register(simulationBehaviourExtension);
		}

		this.simEngine.getEventDispatcher().register(this);

		LOGGER.info("Finished simulation driver initialization.");
	}

	private void registerSimulationBehaviorExtensionInterceptors() throws Exception {
		for (final DecoratedSimulationBehaviorProvider decoratedSimulationBehaviorProvider : decoratedSimulationBehaviorProviders) {
			this.registerSimulationBehaviorExtension(decoratedSimulationBehaviorProvider);
		}
	}
	
	/**
	 * This method registers new providers for the behavior extension.
	 * @param decoratedSimulationBehaviorProvider
	 */
	public void registerSimulationBehaviorExtension(final DecoratedSimulationBehaviorProvider decoratedSimulationBehaviorProvider) throws Exception {
		final ExtensionLoggingInterceptor myLoggingInterceptor = new ExtensionLoggingInterceptor();
		final SchedulingInterceptor schedulingInterceptor = new SchedulingInterceptor(this);
		final SimulationExtensionOnEventContractEnforcementInterceptor contract = new SimulationExtensionOnEventContractEnforcementInterceptor();
		final List<Interceptor> interceptors = List.of(contract, myLoggingInterceptor, schedulingInterceptor);

		behaviorExtensions.add(decoratedSimulationBehaviorProvider.decorateSimulationBehaviorWithInterceptors(interceptors));
	}

	@Override
	public void startSimulation() {
		final DESEvent simulationStart = new SimulationStarted();
		
		//initialize
		simEngine.init();
		// initial events
		simEngine.scheduleEvent(simulationStart);
		// start
		simEngine.start();
	}

	/**
	 * @return
	 */
	public SimulationMonitoring monitorSimulation() {
		// FIXME what would be now the Status.
		return new SimulationStatus(null);
	}

//	@Subscribe
//	public void update(DESEvent evt) {
//		// FIXME:: Check from which of the interested types the event is then delegate
//		// to usageSimulation to find the nextEvent and schedule that event.
//		// FIXME:: The FinishedUserEvent is scheduled somewhere from the component in
//		// the last action which will be interpreted somewhere
//
//		if (evt instanceof UserStarted) {
//			UserStarted startUserEvent = UserStarted.class.cast(evt);
//			LOGGER.info(String.format(
//					"Previously scheduled event '%s' has finished executing its event routine now we could schedule a FinishUserEvent",
//					startUserEvent.getId()));
//			UserFinished userFinished = new UserFinished(startUserEvent.getSimulatedUser());
//			simEngine.scheduleEvent(userFinished);
//
//		}
//
//	}

	@Override
	public void scheduleForSimulation(final DESEvent evt) {
		// all the events are scheduled through this public available method of the
		// simulation driver
		LOGGER.info(EventPrettyLogPrinter.prettyPrint(evt, "Scheduled for simulation", "Simulation Driver"));
		simEngine.scheduleEvent(evt);

	}

	// another convenient method would be scheduleForSimulation of several events
	@Override
	public void scheduleForSimulation(final List<DESEvent> evt) {
		for (final DESEvent desEvent : evt) {
			scheduleForSimulation(desEvent);
		}

	}

}
