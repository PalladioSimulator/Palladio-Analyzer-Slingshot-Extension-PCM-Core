package org.palladiosimulator.analyzer.slingshot.monitor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.palladiosimulator.analyzer.slingshot.monitor.calculator.CalculatorProvider;
import org.palladiosimulator.analyzer.slingshot.monitor.calculator.CalculatorProviderObject;
import org.palladiosimulator.analyzer.slingshot.monitor.probe.DESEventProbe;
import org.palladiosimulator.analyzer.slingshot.monitor.probe.Probed;
import org.palladiosimulator.analyzer.slingshot.monitor.recorder.RecorderAttachingCalculatorFactoryDecorator;
import org.palladiosimulator.analyzer.slingshot.simulation.core.SimulationMonitoring;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.probeframework.ProbeFrameworkContext;
import org.palladiosimulator.probeframework.calculator.Calculator;
import org.palladiosimulator.probeframework.calculator.ExtensibleCalculatorFactoryDelegatingFactory;
import org.palladiosimulator.recorderframework.utils.RecorderExtensionHelper;

/**
 * 
 * @author Julijan Katic
 */
public class Monitoring implements SimulationMonitoring {

	// TODO: Find better way of this map.
	private final Map<Class<? extends DESEvent>, Map<Class<? extends DESEventProbe<?, ?, ?>>, DESEventProbe<?, ?, ?>>> probes;

	private final Set<Calculator> calculators;

	private final ProbeFrameworkContext probeFrameworkContext;

	public Monitoring() {
		this.probes = new HashMap<>();
		this.calculators = new HashSet<Calculator>();
		this.setUpCalculators();
		this.probeFrameworkContext = new ProbeFrameworkContext(new RecorderAttachingCalculatorFactoryDecorator(
				new ExtensibleCalculatorFactoryDelegatingFactory(), "",
				RecorderExtensionHelper.getRecorderConfigurationFactoryForName("")));
	}

	/**
	 * {@inheritDoc}
	 * 
	 * Furthermore, the event should be probable either by being annotated by
	 * {@link ProbeTrigger} or by registering probe providers.
	 */
	@Override
	public void publishProbeEvent(final DESEvent event) {
		final Map<?, DESEventProbe<?, ?, ?>> probes = this.getRegisteredProbes(event.getClass());
		if (probes != null) {
			probes.values().forEach(probe -> probe.takeMeasurement(event));
		}
	}

	private Map<Class<? extends DESEventProbe<?, ?, ?>>, DESEventProbe<?, ?, ?>> getRegisteredProbes(
			final Class<? extends DESEvent> event) {
		return this.probes.get(event);
	}

	private void setUpCalculators() {
		final CalculatorProviderObject calculatorProviderObject = new CalculatorProviderObject();
		final List<Class<?>> classes = calculatorProviderObject.getAllProviders();

		classes.stream()
				.flatMap(clazz -> Arrays.stream(clazz.getMethods()))
				.filter(method -> Modifier.isStatic(method.getModifiers()))
				.filter(method -> method.isAnnotationPresent(CalculatorProvider.class))
				.forEach(this::createCalculator);

	}

	@SuppressWarnings("unchecked")
	private void createCalculator(final Method calculatorProvider) {
		final List<DESEventProbe<?, ?, ?>> calculatorProbes = new ArrayList<>();

		for (final Parameter parameter : calculatorProvider.getParameters()) {
			final Probed probed = parameter.getAnnotation(Probed.class);
			// assume probed != null
			final Class<? extends DESEvent> value = probed.value();
			final Class<? extends DESEventProbe<?, ?, ?>> probeType = (Class<? extends DESEventProbe<?, ?, ?>>) parameter
					.getType();

			this.createProbe(value, probeType);
			calculatorProbes.add(this.probes.get(value).get(probeType));
		}

		try {
			final Calculator calculator = (Calculator) calculatorProvider.invoke(null, calculatorProbes.toArray());
			this.calculators.add(calculator);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void createProbe(final Class<? extends DESEvent> value,
			final Class<? extends DESEventProbe<?, ?, ?>> probeType) {
		if (!this.probes.containsKey(value)) {
			this.probes.put(value, new HashMap<>());
		}
		if (!this.probes.get(value).containsKey(probeType)) {
			try {
				this.probes.get(value).put(probeType,
						probeType.getDeclaredConstructor(value.getClass()).newInstance(value));
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
