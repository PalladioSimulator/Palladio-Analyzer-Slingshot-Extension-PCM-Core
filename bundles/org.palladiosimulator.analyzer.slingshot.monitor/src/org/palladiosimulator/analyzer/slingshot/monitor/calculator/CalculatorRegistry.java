package org.palladiosimulator.analyzer.slingshot.monitor.calculator;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.palladiosimulator.analyzer.slingshot.monitor.probe.DESEventProbe;
import org.palladiosimulator.analyzer.slingshot.monitor.probe.ProbeRegistry;
import org.palladiosimulator.analyzer.slingshot.monitor.probe.Probed;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.probeframework.ProbeFrameworkContext;
import org.palladiosimulator.probeframework.calculator.Calculator;
import org.palladiosimulator.probeframework.calculator.IGenericCalculatorFactory;

public final class CalculatorRegistry {

	private final Map<String, Calculator> calculatorMap;
	private final ProbeRegistry probeRegistry;

	public CalculatorRegistry(final ProbeRegistry probeRegistry) {
		this.calculatorMap = new HashMap<>();
		this.probeRegistry = probeRegistry;
	}

	public Calculator getCalculator(final String name) {
		return this.calculatorMap.get(name);
	}

	@SuppressWarnings("unchecked")
	public void createCalculator(final Method calculatorProvider, final ProbeFrameworkContext probeFrameworkContext) {
		final List<DESEventProbe<?, ?, ?>> calculatorProbes = new ArrayList<>();

		for (final Parameter parameter : calculatorProvider.getParameters()) {
			final Probed probed = parameter.getAnnotation(Probed.class);
			// assume probed != null
			final Class<? extends DESEvent> value = probed.value();
			final Class<? extends DESEventProbe<?, ?, ?>> probeType = (Class<? extends DESEventProbe<?, ?, ?>>) parameter
					.getType();

			final DESEventProbe<?, ?, ?> eventProbe = this.probeRegistry.createProbe(value, probeType);
			calculatorProbes.add(eventProbe);
		}

		final CalculatorProvider annotation = calculatorProvider.getAnnotation(CalculatorProvider.class);
		final String identifier = annotation.id().equals("") ? calculatorProvider.getName() : annotation.id();

		try {
			final IGenericCalculatorFactory genericCalculatorFactory = probeFrameworkContext
					.getGenericCalculatorFactory();

			final Calculator calculator = (Calculator) calculatorProvider.invoke(null, genericCalculatorFactory,
					calculatorProbes.toArray());
			this.calculatorMap.put(identifier, calculator);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
	}
}
