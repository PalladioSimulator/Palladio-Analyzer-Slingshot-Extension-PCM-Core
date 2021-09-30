package org.palladiosimulator.analyzer.slingshot.monitor.processingtype.timedriven;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import javax.measure.Measure;
import javax.measure.quantity.Duration;

import org.jscience.physics.amount.Amount;
import org.palladiosimulator.experimentanalysis.windowaggregators.SlidingWindowAggregator;
import org.palladiosimulator.measurementframework.MeasuringValue;
import org.palladiosimulator.metricspec.MetricDescription;
import org.palladiosimulator.monitorrepository.statisticalcharacterization.StatisticalCharacterizationAggregator;
import org.palladiosimulator.recorderframework.IRecorder;

public class SlidingWindowStatisticalCharacterizationAggregator extends SlidingWindowAggregator {

	private final StatisticalCharacterizationAggregator aggregator;

	public SlidingWindowStatisticalCharacterizationAggregator(final StatisticalCharacterizationAggregator aggregator) {
		this.aggregator = Objects.requireNonNull(aggregator);
	}

	public SlidingWindowStatisticalCharacterizationAggregator(final StatisticalCharacterizationAggregator aggregator,
			final IRecorder... recorders) {
		super(Arrays.asList(recorders));
		this.aggregator = Objects.requireNonNull(aggregator);
	}

	@Override
	public MetricDescription getExpectedWindowDataMetric() {
		return this.aggregator.getDataMetric();
	}

	@Override
	protected MeasuringValue processWindowData(final Iterable<MeasuringValue> windowData,
			final Measure<Double, Duration> windowLeftBound,
			final Measure<Double, Duration> windowLength) {
		final Amount<Duration> leftBound = Amount.valueOf(windowLeftBound.getValue(), windowLeftBound.getUnit());
		final Amount<Duration> length = Amount.valueOf(windowLength.getValue(), windowLength.getUnit());
		final Amount<Duration> rightBound = leftBound.plus(length);

		return this.aggregator.aggregateData(windowData, leftBound, rightBound, Optional.of(length));
	}

}
