package org.palladiosimulator.analyzer.slingshot.monitor.recorder;

import org.apache.log4j.Logger;
import org.palladiosimulator.edp2.models.measuringpoint.MeasuringPoint;
import org.palladiosimulator.measurementframework.MeasuringValue;
import org.palladiosimulator.measurementframework.listener.IMeasurementSourceListener;
import org.palladiosimulator.metricspec.MetricDescription;
import org.palladiosimulator.probeframework.calculator.Calculator;
import org.palladiosimulator.probeframework.calculator.CalculatorProbeSet;
import org.palladiosimulator.probeframework.calculator.IGenericCalculatorFactory;

public class DebuggingAttachingCalculatorFactoryDecorator implements IGenericCalculatorFactory {

	private static final Logger LOGGER = Logger.getLogger(DebuggingAttachingCalculatorFactoryDecorator.class);

	private final IGenericCalculatorFactory calculatorFactory;

	public DebuggingAttachingCalculatorFactoryDecorator(final IGenericCalculatorFactory calculatorFactory) {
		this.calculatorFactory = calculatorFactory;
	}

	@Override
	public Calculator buildCalculator(final MetricDescription metric, final MeasuringPoint measuringPoint,
			final CalculatorProbeSet probeConfiguration) {
		return this.setupDebug(
				this.calculatorFactory.buildCalculator(metric, measuringPoint, probeConfiguration));
	}

	private Calculator setupDebug(final Calculator calculator) {
		calculator.addObserver(new IMeasurementSourceListener() {

			@Override
			public void preUnregister() {
				LOGGER.info("Called unregister on Calculator object: " + calculator);
			}

			@Override
			public void newMeasurementAvailable(final MeasuringValue measuringValue) {
				measuringValue.asList()
						.forEach(measure -> LOGGER
								.info("New value available from <" + calculator + ">: " + measure.toString()));
			}

		});
		return calculator;
	}
}
