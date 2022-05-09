package org.palladiosimulator.analyzer.slingshot.scalingpolicy.interpreter.aggregator;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.measure.Measure;
import javax.measure.quantity.Quantity;

import org.palladiosimulator.measurementframework.MeasuringValue;
import org.palladiosimulator.metricspec.NumericalBaseMetricDescription;
import org.palladiosimulator.monitorrepository.statisticalcharacterization.StatisticalCharacterizationAggregator;

public class MinAggregator extends StatisticalCharacterizationAggregator {
	
	
	public MinAggregator(NumericalBaseMetricDescription expectedDataMetric) {
		super(expectedDataMetric);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Measure<Double, Quantity> calculateStatisticalCharaterizationDiscrete(
			Iterable<MeasuringValue> dataToAggregate) {
		final double min = StreamSupport.stream(dataToAggregate.spliterator(), false)
				.map(this::obtainDataValueFromMeasurement)
				.collect(Collectors.minBy(Double::compare))
				.orElseGet(() -> 0.0);
		
		return Measure.valueOf(min, getDataDefaultUnit());
	}

	@Override
	protected Measure<Double, Quantity> calculateStatisticalCharacterizationContinuous(
			Iterable<MeasuringValue> dataToAggregate) {
		// TODO Auto-generated method stub
		return null;
	}

}
