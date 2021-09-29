package org.palladiosimulator.analyzer.slingshot.monitor.interpretation;

import org.palladiosimulator.analyzer.slingshot.simulation.api.SimulationScheduling;
import org.palladiosimulator.analyzer.slingshot.simulation.core.events.ConfigurationStarted;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.SimulationBehaviorExtension;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.results.ResultEvent;
import org.palladiosimulator.monitorrepository.MonitorRepository;
import org.palladiosimulator.probeframework.ProbeFrameworkContext;
import org.palladiosimulator.probeframework.calculator.IGenericCalculatorFactory;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

/**
 * This behavior is supposed to interpret any monitor model files. It creates
 * instances of monitors with respect to the models.
 * 
 * @author Julijan Katic
 */
public class MonitorModelBehavior implements SimulationBehaviorExtension {

	/** The repository of monitors to interpret. */
	private final MonitorRepository monitorRepository;

	/** Factory for creating calculators per measuring spec. */
	private final IGenericCalculatorFactory calculatorFactory;

	private final ProbeFrameworkContext probeFrameworkContext;

	private final SimulationScheduling simulationScheduling;

	@Inject
	public MonitorModelBehavior(
			final MonitorRepository monitorRepository,
			final IGenericCalculatorFactory calculatorFactory,
			final ProbeFrameworkContext probeFrameworkContext,
			final SimulationScheduling scheduling) {
		this.monitorRepository = monitorRepository;
		this.calculatorFactory = calculatorFactory;
		this.probeFrameworkContext = probeFrameworkContext;
		this.simulationScheduling = scheduling;
	}

	/**
	 * Interprets the monitor model and returns all necessary events for either
	 * continuing interpretation, or for stating that a calculator has been
	 * registered.
	 */
	@Subscribe
	public ResultEvent<?> onConfigurationStarted(final ConfigurationStarted configurationStarted) {
		final MonitorRepositoryInterpreter monitorRepositoryInterpreter = new MonitorRepositoryInterpreter(
				this.probeFrameworkContext.getCalculatorRegistry(), this.simulationScheduling);

		return ResultEvent.of(monitorRepositoryInterpreter.doSwitch(this.monitorRepository));
	}

}
