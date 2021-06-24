package org.palladiosimulator.analyzer.slingshot.monitor.calculator;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.palladiosimulator.analyzer.slingshot.monitor.probe.DESEventProbe;
import org.palladiosimulator.analyzer.slingshot.monitor.probe.EventToRequestContextMapper;
import org.palladiosimulator.analyzer.slingshot.monitor.probe.ProbeRegistry;
import org.palladiosimulator.analyzer.slingshot.monitor.probe.Probed;
import org.palladiosimulator.analyzer.slingshot.simulation.api.PCMPartitionManager;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.probeframework.ProbeFrameworkContext;
import org.palladiosimulator.probeframework.calculator.Calculator;

import com.google.inject.Injector;

public final class CalculatorRegistry {

	private final Map<String, Calculator> calculatorMap;
	private final ProbeRegistry probeRegistry;
	private final PCMPartitionManager pcmPartitionManager;

	public CalculatorRegistry(final ProbeRegistry probeRegistry, final PCMPartitionManager pcmPartitionManager) {
		this.calculatorMap = new HashMap<>();
		this.probeRegistry = probeRegistry;
		this.pcmPartitionManager = pcmPartitionManager;
	}

	public Calculator getCalculator(final String name) {
		return this.calculatorMap.get(name);
	}

	@SuppressWarnings("unchecked")
	private void createCalculator(final AbstractCalculatorProviders instance,
			final Method calculatorProvider, final ProbeFrameworkContext probeFrameworkContext) {
		try {
			final List<DESEventProbe<?, ?, ?>> parametersForCalculatorProvider = new ArrayList<>();

			final CalculatorProvider annotation = calculatorProvider.getAnnotation(CalculatorProvider.class);
			final String identifier = annotation.id().equals("") ? calculatorProvider.getName() : annotation.id();
			final EventToRequestContextMapper mapper = annotation.requestContextMapper().getConstructor().newInstance();

			for (final Parameter parameter : calculatorProvider.getParameters()) {
				final Probed probed = parameter.getAnnotation(Probed.class);
				if (probed != null) {
					final Class<? extends DESEvent> value = probed.value();
					final Class<? extends DESEventProbe<?, ?, ?>> probeType = (Class<? extends DESEventProbe<?, ?, ?>>) parameter
							.getType();

					final DESEventProbe<?, ?, ?> eventProbe = this.probeRegistry.createProbe(value, probeType, mapper);
					parametersForCalculatorProvider.add(eventProbe);
				}
			}

			final Calculator calculator = (Calculator) calculatorProvider.invoke(instance,
					parametersForCalculatorProvider.toArray());
			this.calculatorMap.put(identifier, calculator);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException
				| NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}

	}

	public void createCalculators(final Class<? extends AbstractCalculatorProviders> clazz,
			final ProbeFrameworkContext probeFrameworkContext, final Injector calculatorInjector) {
		try {
			final AbstractCalculatorProviders calculatorProviders = calculatorInjector.getInstance(clazz);

			Arrays.stream(clazz.getMethods())
					.filter(method -> method.isAnnotationPresent(CalculatorProvider.class))
					.forEach(method -> this.createCalculator(calculatorProviders, method, probeFrameworkContext));
		} catch (IllegalArgumentException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}
}
