package org.palladiosimulator.analyzer.slingshot.scalingpolicy.interpreter.aggregator;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.measure.Measure;
import javax.measure.quantity.Quantity;

import org.jscience.physics.amount.Amount;
import org.palladiosimulator.measurementframework.MeasuringValue;
import org.palladiosimulator.metricspec.NumericalBaseMetricDescription;
import org.palladiosimulator.monitorrepository.statisticalcharacterization.StatisticalCharacterizationAggregator;

/**
 * This Aggregator tries to find the maximum of an iteration.
 * 
 * For the continuous case, we use the <a href="https://en.wikipedia.org/wiki/Golden-section_search">Golden Section Search</a>
 * algorithm.
 * 
 * @author Julijan Katic
 */
public class MaxAggregator extends StatisticalCharacterizationAggregator {
	
	private static final double INVPHI = (Math.sqrt(5.0) - 1) / 2.0;
	private static final double INVPHI2 = (3 - Math.sqrt(5.0)) / 2.0;
	
	private final double tol;

	public MaxAggregator(final NumericalBaseMetricDescription expectedDataMetric) {
		this(expectedDataMetric, 1e-5);
	}
	
	public MaxAggregator(final NumericalBaseMetricDescription expectedDataMetric, final double tol) {
		super(expectedDataMetric);
		this.tol = tol;
	}

	@Override
	protected Measure<Double, Quantity> calculateStatisticalCharaterizationDiscrete(
			Iterable<MeasuringValue> dataToAggregate) {
		final double max = StreamSupport.stream(dataToAggregate.spliterator(), false)
				.map(this::obtainDataValueFromMeasurement)
				.collect(Collectors.maxBy(Double::compare))
				.orElseGet(() -> 0.0);
		
		return Measure.valueOf(max, getDataDefaultUnit());
	}

	@Override
	protected Measure<Double, Quantity> calculateStatisticalCharacterizationContinuous(
			Iterable<MeasuringValue> dataToAggregate) {
		// TODO: Do we even need this?
		throw new UnsupportedOperationException("Currently not able to find local maximum of continuous data");
	}

}
