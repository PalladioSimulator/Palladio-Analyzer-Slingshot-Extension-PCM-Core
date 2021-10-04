package org.palladiosimulator.analyzer.slingshot.monitor;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.palladiosimulator.analyzer.slingshot.monitor.recorder.decorators.MonitoringAttachingCalculatorFactoryDecorator;
import org.palladiosimulator.analyzer.slingshot.simulation.api.SimulationMonitoring;
import org.palladiosimulator.analyzer.slingshot.simulation.api.SimulationScheduling;
import org.palladiosimulator.probeframework.ProbeFrameworkContext;
import org.palladiosimulator.probeframework.calculator.ExtensibleCalculatorFactoryDelegatingFactory;
import org.palladiosimulator.probeframework.calculator.IGenericCalculatorFactory;

import de.uka.ipd.sdq.simucomframework.SimuComConfig;
import de.uka.ipd.sdq.simucomframework.calculator.RecorderAttachingCalculatorFactoryDecorator;

/**
 * This implements the SimulationMonitoring interface of Slingshot.
 * <p>
 * Each Monitor also provides a {@link ProbeFrameworkContext} and a
 * {@link IGenericCalculatorFactory}.
 * 
 * @author Julijan Katic
 */
@Singleton
public class GeneralMonitor implements SimulationMonitoring {

	/**
	 * The Scheduling which is needed to add events outside from the main event
	 * handlers.
	 */
	private final SimulationScheduling scheduling;

	/** The (launch) configuration which is needed for adding a recorder */
	private final SimuComConfig simuComConfig;

	/** The context of all probes and calculators. */
	private final ProbeFrameworkContext probeFrameworkContext;

	/** The calculator factory. */
	private final IGenericCalculatorFactory calculatorFactory;

	/**
	 * Constructs a monitor. It sets up a calculator factory and initializes the
	 * {@link ProbeFrameworkContext}.
	 * 
	 * @param simuComConfig The launch configuration needed for the recorder.
	 * @param scheduling    The scheduler to add events outside from the event
	 *                      handlers (since the events are created from observers
	 *                      which cannot return).
	 */
	@Inject
	public GeneralMonitor(
			final SimuComConfig simuComConfig, final SimulationScheduling scheduling) {
		this.scheduling = scheduling;
		this.simuComConfig = simuComConfig;
		this.calculatorFactory = this.setupCalculatorFactory(this.simuComConfig);
		this.probeFrameworkContext = new ProbeFrameworkContext(this.calculatorFactory);
	}

	/**
	 * Helper method for setting up a factory from the launch configuration. The
	 * launch configuration is needed to setup a recorder, which then attaches
	 * itself to the calculators on build.
	 * 
	 * @param simuComConfig The launch configuration.
	 * @return A non-{@code null} calculator factory.
	 */
	private IGenericCalculatorFactory setupCalculatorFactory(final SimuComConfig simuComConfig) {
		final IGenericCalculatorFactory parentFactory = new ExtensibleCalculatorFactoryDelegatingFactory();
		final IGenericCalculatorFactory recorderAttachedFactory = new RecorderAttachingCalculatorFactoryDecorator(
				parentFactory, simuComConfig.getRecorderName(), simuComConfig.getRecorderConfigurationFactory());
		final IGenericCalculatorFactory monitorAttachedFactory = new MonitoringAttachingCalculatorFactoryDecorator(
				recorderAttachedFactory, new EventBasedMeasurementObserver(this.scheduling));

		return monitorAttachedFactory;
	}

	/**
	 * {@inheritDoc} In this implementation, it does nothing.
	 */
	@Override
	public void init() {
	}

	@Override
	public IGenericCalculatorFactory getCalculatorFactory() {
		return this.calculatorFactory;
	}

	@Override
	public ProbeFrameworkContext getProbeFrameworkContext() {
		return this.probeFrameworkContext;
	}

}
