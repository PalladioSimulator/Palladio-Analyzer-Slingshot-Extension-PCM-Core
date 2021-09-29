package org.palladiosimulator.analyzer.slingshot.simulation.driver;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.monitor.data.MeasurementMade;
import org.palladiosimulator.analyzer.slingshot.simulation.api.Simulation;
import org.palladiosimulator.analyzer.slingshot.simulation.api.SimulationEngine;
import org.palladiosimulator.analyzer.slingshot.simulation.api.SimulationScheduling;
import org.palladiosimulator.analyzer.slingshot.simulation.core.events.ConfigurationStarted;
import org.palladiosimulator.analyzer.slingshot.simulation.core.events.SimulationFinished;
import org.palladiosimulator.analyzer.slingshot.simulation.core.events.SimulationStarted;
import org.palladiosimulator.analyzer.slingshot.simulation.core.exceptions.EventContractException;
import org.palladiosimulator.analyzer.slingshot.simulation.driver.behavior.interceptors.DefaultInterceptorModule;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.events.EventPrettyLogPrinter;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.BehaviorContainer;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.EventContract;

import com.google.inject.Injector;

import de.uka.ipd.sdq.simucomframework.SimuComConfig;

/**
 * The SimulationDriver simulates and executes the registered behaviors and its
 * interceptors. The EventBus is provided by a {@link SimulationEngine}.
 * <p>
 * When the simulation starts, it will always fire a {@link SimulationStarted}
 * event at the beginning. Afterwards, all the event listeners to this event
 * will be called. When no event is called again, then {@link SimulationFinshed}
 * event will be called one last time so that extensions can clear things up.
 * 
 * 
 * @author Julijan Katic
 */
@Singleton
public final class SimulationDriver implements Simulation, SimulationScheduling {

	private static final Logger LOGGER = Logger.getLogger(SimulationDriver.class);

	/**
	 * The simulation engine that is responsible for dispatching events.
	 */
	private final SimulationEngine engine;

	private final EventGlobalContractVerifier eventVerifier;

	/**
	 * Specifies whether the simulation has started.
	 */
	private boolean simulationStarted = false;

	/**
	 * Saves the state whether the driver has been initialized already.
	 */
	private boolean driverInitialized = false;

	/**
	 * The parent injector that is used to create child injectors for the simulation
	 * behavior extensions.
	 */
	private final Injector parentInjector;

	/** The configuration. */
	private final SimuComConfig simuComConfig;

	/** Counter for the stopping condition for measurements. */
	private long measurementsMade = 0;

	/**
	 * Instantiates the driver with an engine and the parent injector. This will
	 * also add an Event Exception Handler.
	 * 
	 * @param simEngine The non-null simulation engine.
	 * @param injector  The injector that is needed to initialize the simulation
	 *                  behavior extensions.
	 */
	@Inject
	public SimulationDriver(final SimulationEngine engine,
			final Injector injector,
			final SimuComConfig simuComConfig) {
		this.engine = engine;
		this.parentInjector = injector;
		this.simuComConfig = simuComConfig;
		this.eventVerifier = new EventGlobalContractVerifier();
	}

	@Override
	public void init() throws Exception {
		if (this.driverInitialized) {
			return;
		}

		LOGGER.info("Start simulation driver initialization.");

		final BehaviorContainer container = new BehaviorContainer();
		final Injector simulationChildInjector = this.parentInjector
				.createChildInjector(
						container,
						new DefaultInterceptorModule(this));

		container.loadExtensions(simulationChildInjector);

		/* Register every extension behavior to the dispatcher */
		container.getExtensions().stream().forEach(extension -> {
			extension.init();
			this.engine.registerEventListener(extension);
			LOGGER.info(
					"Registered behavior extension to the event dispatcher: " + extension.getClass().getSimpleName());
		});

		this.driverInitialized = true;
		LOGGER.info("Finished simulation driver initialization");
	}

	@Override
	public void scheduleForSimulation(final DESEvent event) {
		if (!this.simulationStarted) {
			LOGGER.debug("Couldn't publish event: " + event.getId());
			return;
		}

		LOGGER.info(EventPrettyLogPrinter.prettyPrint(event, "Scheduled for simulation", "Simulation Driver"));

		if (event instanceof MeasurementMade) {
			/* We want to count if a measurement was made for a stopping condition. */
			this.measurementsMade++;
		}

		try {
			this.checkEventContract(event);
			this.engine.scheduleEvent(event);

			if (this.simuComConfig.getMaxMeasurementsCount() > 0 &&
					this.measurementsMade >= this.simuComConfig.getMaxMeasurementsCount()) {
				this.stopSimulation();
			}
		} catch (final EventContractException e) {
			LOGGER.error("Couldn't publish event.", e);
		}
	}

	@Override
	public void scheduleForSimulation(final List<DESEvent> events) {
		if (!this.simulationStarted) {
			LOGGER.debug("Couldn't publish events");
			return;
		}
		events.forEach(this::scheduleForSimulation);
	}

	@Override
	public void checkEventContract(final DESEvent event) throws EventContractException {
		final EventContract contract = event.getClass().getAnnotation(EventContract.class);

		if (contract != null) {
			final int numberOfSpawns = this.eventVerifier.getNumberOfSpawns(event, 0);

			if (contract.maximalPublishing() <= numberOfSpawns) {
				throw new EventContractException(contract, event, "Maximum number of publishments reached.");
			}

			this.eventVerifier.incrementSpawn(event, numberOfSpawns + 1);
		}
	}

	@Override
	public void startSimulation() {
		this.simulationStarted = true;

		this.engine.init();
		/* Start Configuration */
		this.engine.scheduleEvent(new ConfigurationStarted(this.simuComConfig));
		/* Start the simulation */
		this.engine.scheduleEvent(new SimulationStarted());
		/* Stop simulation as configured after a certain time. */
		this.engine.scheduleEvent(new SimulationFinished(this.simuComConfig.getSimuTime()));

		LOGGER.debug("Start simulation");
		this.engine.start();

		LOGGER.debug("Last event was dispatched. Finish simulation.");
	}

	@Override
	public void stopSimulation() {
		this.simulationStarted = false;
		this.engine.scheduleEvent(new SimulationFinished());
	}

}
