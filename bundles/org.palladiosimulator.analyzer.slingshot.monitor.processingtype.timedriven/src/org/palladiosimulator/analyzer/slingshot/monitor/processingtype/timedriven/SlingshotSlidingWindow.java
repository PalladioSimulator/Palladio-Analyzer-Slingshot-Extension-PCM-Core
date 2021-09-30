package org.palladiosimulator.analyzer.slingshot.monitor.processingtype.timedriven;

import javax.measure.Measurable;
import javax.measure.Measure;
import javax.measure.quantity.Duration;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;

import org.palladiosimulator.experimentanalysis.ISlidingWindowMoveOnStrategy;
import org.palladiosimulator.experimentanalysis.SlidingWindow;
import org.palladiosimulator.metricspec.MetricDescription;

public final class SlingshotSlidingWindow extends SlidingWindow {

	private double currentSimulationTime;

	public SlingshotSlidingWindow(final Measure<Double, Duration> windowLength,
			final Measure<Double, Duration> increment,
			final Measure<Double, Duration> initialLowerBound, final MetricDescription acceptedMetrics,
			final ISlidingWindowMoveOnStrategy moveOnStrategy) {
		super(windowLength, increment, initialLowerBound, acceptedMetrics, moveOnStrategy);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Measure<Double, Duration> getCurrentUpperBound() {
		final double lowerBoundValue = this.getCurrentLowerBound().getValue();
		final Unit<Duration> unit = this.getCurrentLowerBound().getUnit();

		final double upperBoundValue = Math.min(lowerBoundValue + this.getSpecifiedWindowLength().doubleValue(unit),
				this.currentSimulationTime);

		return Measure.valueOf(upperBoundValue, unit);
	}

	@Override
	public Measure<Double, Duration> getEffectiveWindowLength() {
		final Unit<Duration> unit = this.getCurrentLowerBound().getUnit();
		return Measure.valueOf(this.getCurrentUpperBound().doubleValue(unit) - this.getCurrentLowerBound().getValue(),
				unit);
	}

	void updateSimulationTime(final double simulationTime) {
		this.currentSimulationTime = simulationTime;
	}

	void onSimulationStop() {
		final Measurable<Duration> effectiveWindowLength = this.getEffectiveWindowLength();
		if (effectiveWindowLength.doubleValue(SI.SECOND) != 0) {
			this.onWindowFullEvent();
		}
	}
}
