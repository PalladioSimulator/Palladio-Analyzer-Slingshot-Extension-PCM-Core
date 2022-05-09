package org.palladiosimulator.analyzer.slingshot.monitor.recorder.decorators;

import org.palladiosimulator.analyzer.slingshot.monitor.EventBasedMeasurementObserver;
import org.palladiosimulator.analyzer.slingshot.monitor.data.SlingshotMeasuringValue;
import org.palladiosimulator.edp2.models.measuringpoint.MeasuringPoint;
import org.palladiosimulator.measurementframework.MeasuringValue;
import org.palladiosimulator.measurementframework.listener.IMeasurementSourceListener;
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
	public Calculator buildCalculator(final MetricDescription metricDescription, final MeasuringPoint measuringPoint,
			final CalculatorProbeSet calculatorProbeSet) {
		return this.setupCalculator(this.delegator.buildCalculator(metricDescription, measuringPoint, calculatorProbeSet));
	}

	private Calculator setupCalculator(final Calculator calculator) {
		calculator.addObserver(new IMeasurementSourceListener() {
			
			public void preUnregister() {
				observer.preUnregister();
			}
			
			public void newMeasurementAvailable(final MeasuringValue newMeasurement) {
				final SlingshotMeasuringValue measuringValue = new SlingshotMeasuringValue(newMeasurement, calculator.getMeasuringPoint());
				observer.newMeasurementAvailable(measuringValue);
			}
			
		});
		return calculator;
	}

}
