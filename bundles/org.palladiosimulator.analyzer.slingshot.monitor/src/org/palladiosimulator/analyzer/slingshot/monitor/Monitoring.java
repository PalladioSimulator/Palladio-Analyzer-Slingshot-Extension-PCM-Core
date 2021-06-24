package org.palladiosimulator.analyzer.slingshot.monitor;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.palladiosimulator.analyzer.slingshot.monitor.calculator.AbstractCalculatorProviders;
import org.palladiosimulator.analyzer.slingshot.monitor.calculator.CalculatorProvider;
import org.palladiosimulator.analyzer.slingshot.monitor.calculator.CalculatorProviderObject;
import org.palladiosimulator.analyzer.slingshot.monitor.calculator.CalculatorRegistry;
import org.palladiosimulator.analyzer.slingshot.monitor.probe.ProbeRegistry;
import org.palladiosimulator.analyzer.slingshot.monitor.probe.ProbeRegistry.SingletonProbeInstanceMap;
import org.palladiosimulator.analyzer.slingshot.monitor.recorder.RecorderAttachingCalculatorFactoryDecorator;
import org.palladiosimulator.analyzer.slingshot.simulation.api.PCMPartitionManager;
import org.palladiosimulator.analyzer.slingshot.simulation.api.SimulationMonitoring;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.edp2.models.measuringpoint.MeasuringPointRepository;
import org.palladiosimulator.edp2.models.measuringpoint.MeasuringpointFactory;
import org.palladiosimulator.probeframework.ProbeFrameworkContext;
import org.palladiosimulator.probeframework.calculator.ExtensibleCalculatorFactoryDelegatingFactory;
import org.palladiosimulator.probeframework.calculator.IGenericCalculatorFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;

import de.uka.ipd.sdq.simucomframework.SimuComConfig;

/**
 * A controller class that controls the probes, calculators as well as recorders
 * in this system.
 * <p>
 * The monitoring listens to {@link DESEvent}s, and if this event can be probed,
 * then a probe will be taken which could possibly lead to a calculation.
 * <p>
 * The probable events are identified through the definition of calculators. For
 * that, see {@link CalculatorProvider}.
 * 
 * @author Julijan Katic
 */
public final class Monitoring implements SimulationMonitoring {

	/** The collection of probes */
	private final ProbeRegistry probeRegistry;

	/** The collection of calculators. */
	private final CalculatorRegistry calculatorRegistry;

	/** A context for probes as needed by the probe framework. */
	private final ProbeFrameworkContext probeFrameworkContext;

	/**
	 * The parent injector to be used for creating a sub-injector for calculators.
	 */
	private final Injector parentInjector;

	private final MonitoringBehavior monitoringBehavior;

	@Inject
	public Monitoring(final SimuComConfig config,
			final PCMPartitionManager pcmPartitionManager,
			final MonitoringBehavior monitoringBehavior,
			final Injector injector) {
		this.probeRegistry = new ProbeRegistry();
		this.calculatorRegistry = new CalculatorRegistry(this.probeRegistry, pcmPartitionManager);
		this.probeFrameworkContext = new ProbeFrameworkContext(new RecorderAttachingCalculatorFactoryDecorator(
				new ExtensibleCalculatorFactoryDelegatingFactory(), config.getRecorderName(),
				config.getRecorderConfigurationFactory(), monitoringBehavior));
		this.parentInjector = injector;
		this.setUpCalculators();
		this.monitoringBehavior = monitoringBehavior;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * Furthermore, the event should be probable either by being annotated by
	 * {@link ProbeTrigger} or by registering probe providers.
	 */
	@Override
	public void publishProbeEvent(final DESEvent event) {
		final SingletonProbeInstanceMap probes = this.probeRegistry.getProbeMapFor(event.getClass());
		if (probes != null) {
			probes.getSingletonInstances().values().forEach(probe -> probe.takeMeasurement(event));
		}
	}

	/**
	 * Helper method that sets up the calculators that are defined in the extension
	 * point. By setting up the calculator, the probes will be identified as well.
	 */
	private void setUpCalculators() {
		final CalculatorProviderObject calculatorProviderObject = new CalculatorProviderObject();
		final List<Class<? extends AbstractCalculatorProviders>> classes = calculatorProviderObject.getAllProviders();

		final Injector calculatorInjector = this.parentInjector.createChildInjector(new AbstractModule() {
			@Override
			protected void configure() {
				super.configure();
				classes.forEach(this::bind);
			}

			@Provides
			@Singleton
			public MeasuringPointRepository measuringPointRepository() {
				return MeasuringpointFactory.eINSTANCE.createMeasuringPointRepository();
			}

			@Provides
			@Singleton
			public IGenericCalculatorFactory genericCalculatorFactory() {
				return Monitoring.this.probeFrameworkContext.getGenericCalculatorFactory();
			}
		});

		classes.forEach(clazz -> this.calculatorRegistry.createCalculators(clazz, this.probeFrameworkContext,
				calculatorInjector));
	}

}
