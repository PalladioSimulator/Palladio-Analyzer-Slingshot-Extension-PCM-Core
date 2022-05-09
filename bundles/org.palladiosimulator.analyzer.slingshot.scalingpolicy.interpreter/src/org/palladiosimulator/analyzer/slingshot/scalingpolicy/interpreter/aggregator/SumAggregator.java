package org.palladiosimulator.analyzer.slingshot.scalingpolicy.interpreter.aggregator;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.measure.Measure;
import javax.measure.quantity.Quantity;

import org.palladiosimulator.measurementframework.MeasuringValue;
import org.palladiosimulator.metricspec.NumericalBaseMetricDescription;
import org.palladiosimulator.monitorrepository.statisticalcharacterization.StatisticalCharacterizationAggregator;

public class SumAggregator extends StatisticalCharacterizationAggregator {

	public SumAggregator(NumericalBaseMetricDescription expectedDataMetric) {
		super(expectedDataMetric);
	}

	@Override
	protected Measure<Double, Quantity> calculateStatisticalCharaterizationDiscrete(
			Iterable<MeasuringValue> dataToAggregate) {
		final double sum = StreamSupport.stream(dataToAggregate.spliterator(), false)
				.collect(Collectors.summingDouble(this::obtainDataValueFromMeasurement));
				
		return Measure.valueOf(sum, getDataDefaultUnit());
	}

	@Override
	protected Measure<Double, Quantity> calculateStatisticalCharacterizationContinuous(
			Iterable<MeasuringValue> dataToAggregate) {
		throw new UnsupportedOperationException();
	}

}
