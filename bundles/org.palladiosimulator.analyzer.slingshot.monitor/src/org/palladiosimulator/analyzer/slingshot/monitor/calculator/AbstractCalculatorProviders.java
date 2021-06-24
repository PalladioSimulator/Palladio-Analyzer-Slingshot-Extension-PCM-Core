package org.palladiosimulator.analyzer.slingshot.monitor.calculator;

import javax.inject.Inject;

import org.palladiosimulator.edp2.models.measuringpoint.MeasuringPoint;
import org.palladiosimulator.edp2.models.measuringpoint.MeasuringPointRepository;
import org.palladiosimulator.probeframework.calculator.IGenericCalculatorFactory;

public abstract class AbstractCalculatorProviders {

	private final IGenericCalculatorFactory calculatorFactory;

	@Inject
	protected AbstractCalculatorProviders(final IGenericCalculatorFactory genericCalculatorFactory) {
		this.calculatorFactory = genericCalculatorFactory;
	}

	protected IGenericCalculatorFactory getCalculatorFactory() {
		return this.calculatorFactory;
	}

	protected MeasuringPoint initializeMeasuringPoint(final MeasuringPoint measuringPoint,
			final MeasuringPointRepository measuringPointRepository) {
		measuringPoint.setMeasuringPointRepository(measuringPointRepository);
		measuringPointRepository.getMeasuringPoints().add(measuringPoint);
		return measuringPoint;
	}
}
