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

@Singleton
public class GeneralMonitor implements SimulationMonitoring {

	public static final String EXTENSION_POINT_ID = "org.palladiosimulator.analyzer.slingshot.monitor";
	public static final String EXTENSION_POINT_ATTRIBUTE = "delegate";

	private final SimulationScheduling scheduling;
	private final SimuComConfig simuComConfig;
	private final ProbeFrameworkContext probeFrameworkContext;
	private final IGenericCalculatorFactory calculatorFactory;

	@Inject
	public GeneralMonitor(
			final SimuComConfig simuComConfig, final SimulationScheduling scheduling) {
		this.scheduling = scheduling;
		this.simuComConfig = simuComConfig;
		this.calculatorFactory = this.setupCalculatorFactory(this.simuComConfig);
		this.probeFrameworkContext = new ProbeFrameworkContext(this.calculatorFactory);
	}

	private IGenericCalculatorFactory setupCalculatorFactory(final SimuComConfig simuComConfig) {
		final IGenericCalculatorFactory parentFactory = new ExtensibleCalculatorFactoryDelegatingFactory();
		final IGenericCalculatorFactory recorderAttachedFactory = new RecorderAttachingCalculatorFactoryDecorator(
				parentFactory, simuComConfig.getRecorderName(), simuComConfig.getRecorderConfigurationFactory());
		final IGenericCalculatorFactory monitorAttachedFactory = new MonitoringAttachingCalculatorFactoryDecorator(
				recorderAttachedFactory, new EventBasedMeasurementObserver(this.scheduling));

		return monitorAttachedFactory;
	}

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
