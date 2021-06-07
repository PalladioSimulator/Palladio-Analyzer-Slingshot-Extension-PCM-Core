package org.palladiosimulator.analyzer.slingshot.monitor;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.palladiosimulator.analyzer.slingshot.monitor.calculator.CalculatorProvider;
import org.palladiosimulator.analyzer.slingshot.monitor.calculator.CalculatorProviderObject;
import org.palladiosimulator.analyzer.slingshot.monitor.calculator.CalculatorRegistry;
import org.palladiosimulator.analyzer.slingshot.monitor.probe.ProbeRegistry;
import org.palladiosimulator.analyzer.slingshot.monitor.probe.ProbeRegistry.SingletonProbeInstanceMap;
import org.palladiosimulator.analyzer.slingshot.monitor.recorder.RecorderAttachingCalculatorFactoryDecorator;
import org.palladiosimulator.analyzer.slingshot.simulation.core.SimulationMonitoring;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.probeframework.ProbeFrameworkContext;
import org.palladiosimulator.probeframework.calculator.ExtensibleCalculatorFactoryDelegatingFactory;
import org.palladiosimulator.recorderframework.utils.RecorderExtensionHelper;

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

	@Inject
	public Monitoring() {
		this.probeRegistry = new ProbeRegistry();
		this.calculatorRegistry = new CalculatorRegistry(this.probeRegistry);
		this.probeFrameworkContext = new ProbeFrameworkContext(new RecorderAttachingCalculatorFactoryDecorator(
				new ExtensibleCalculatorFactoryDelegatingFactory(), "",
				RecorderExtensionHelper.getRecorderConfigurationFactoryForName("")));
		this.setUpCalculators();
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
		final List<Class<?>> classes = calculatorProviderObject.getAllProviders();

		classes.stream()
				.flatMap(clazz -> Arrays.stream(clazz.getMethods()))
				.filter(method -> Modifier.isStatic(method.getModifiers()))
				.filter(method -> method.isAnnotationPresent(CalculatorProvider.class))
				.forEach(method -> this.calculatorRegistry.createCalculator(method, this.probeFrameworkContext));

	}

}
