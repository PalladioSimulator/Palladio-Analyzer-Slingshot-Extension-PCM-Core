package org.palladiosimulator.analyzer.slingshot.monitor.recorder.decorators;

import org.palladiosimulator.analyzer.slingshot.monitor.EventBasedMeasurementObserver;
import org.palladiosimulator.edp2.models.measuringpoint.MeasuringPoint;
import org.palladiosimulator.metricspec.MetricDescription;
import org.palladiosimulator.probeframework.calculator.Calculator;
import org.palladiosimulator.probeframework.calculator.CalculatorProbeSet;
import org.palladiosimulator.probeframework.calculator.IGenericCalculatorFactory;

public final class MonitoringAttachingCalculatorFactoryDecorator implements IGenericCalculatorFactory {

	private final IGenericCalculatorFactory delegator;
	private final EventBasedMeasurementObserver observer;

	public MonitoringAttachingCalculatorFactoryDecorator(final IGenericCalculatorFactory delegator,
			final EventBasedMeasurementObserver observer) {
		this.delegator = delegator;
		this.observer = observer;
	}

	@Override
	public Calculator buildCalculator(final MetricDescription arg0, final MeasuringPoint arg1,
			final CalculatorProbeSet arg2) {
		return this.setupCalculator(this.delegator.buildCalculator(arg0, arg1, arg2));
	}

	private Calculator setupCalculator(final Calculator calculator) {
		calculator.addObserver(this.observer);
		return calculator;
	}
}
