package org.palladiosimulator.analyzer.slingshot.simulation.core;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.simulation.api.Simulation;
import org.palladiosimulator.analyzer.slingshot.simulation.core.events.SimulationStarted;
import org.palladiosimulator.analyzer.slingshot.simulation.engine.SimulationEngine;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.events.EventPrettyLogPrinter;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.SimulationBehaviorExtension;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.decorators.DecoratedSimulationBehaviorProvider;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.interceptors.ExtensionLoggingInterceptor;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.interceptors.SchedulingInterceptor;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.interceptors.SimulationExtensionOnEventContractEnforcementInterceptor;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.interceptor.Interceptor;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.model.SimulationModel;

public class SimulationDriver implements Simulation, SimulationScheduling {

	private final Logger LOGGER = Logger.getLogger(SimulationDriver.class);

	private final SimulationEngine simEngine;

	private final List<SimulationBehaviorExtension> behaviorExtensions;

	private final List<DecoratedSimulationBehaviorProvider> decoratedSimulationBehaviorProviders;

	public SimulationDriver(final SimulationEngine simEngine,
			final List<DecoratedSimulationBehaviorProvider> decoratedSimProviders) {
		this.simEngine = simEngine;
		this.behaviorExtensions = new ArrayList<SimulationBehaviorExtension>();
		this.decoratedSimulationBehaviorProviders = new ArrayList<DecoratedSimulationBehaviorProvider>();
		this.decoratedSimulationBehaviorProviders.addAll(decoratedSimProviders);
	}

	@Override
	public void init(final SimulationModel model) throws Exception {
		LOGGER.info("Start simulation driver initialization.");

		registerSimulationBehaviorExtensionInterceptors();

		for (final SimulationBehaviorExtension simulationBehaviorExtension : behaviorExtensions) {
			simulationBehaviorExtension.init(model);
			this.simEngine.getEventDispatcher().register(simulationBehaviorExtension);
			LOGGER.info("Registered behavior extension to the event dispatcher: "
					+ simulationBehaviorExtension.getClass().getSimpleName());
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
	 * 
	 * @param decoratedSimulationBehaviorProvider
	 */
	public void registerSimulationBehaviorExtension(
			final DecoratedSimulationBehaviorProvider decoratedSimulationBehaviorProvider) throws Exception {

		LOGGER.info("Add all the interceptors to " + decoratedSimulationBehaviorProvider.getClass().getSimpleName());

		final ExtensionLoggingInterceptor myLoggingInterceptor = new ExtensionLoggingInterceptor();
		final SchedulingInterceptor schedulingInterceptor = new SchedulingInterceptor(this);
		final SimulationExtensionOnEventContractEnforcementInterceptor contract = new SimulationExtensionOnEventContractEnforcementInterceptor();

		final List<Interceptor> interceptors = List.of(contract, myLoggingInterceptor, schedulingInterceptor);

		behaviorExtensions
				.add(decoratedSimulationBehaviorProvider.decorateSimulationBehaviorWithInterceptors(interceptors));
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
		// FIXME what would be now the Status.
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

}
