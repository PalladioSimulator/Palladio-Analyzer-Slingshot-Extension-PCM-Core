package org.palladiosimulator.analyzer.slingshot.scalingpolicy.interpreter;

import javax.measure.Measure;
import javax.measure.quantity.Dimensionless;
import javax.measure.unit.Unit;

import org.palladiosimulator.analyzer.slingshot.monitor.data.SlingshotMeasuringValue;
import org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.MeasuringPointTriggerContextMapper;
import org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.PointInTimeTriggered;
import org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.ScalingTriggerPredicate;
import org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.TriggerContext;
import org.palladiosimulator.analyzer.slingshot.simulation.api.SimulationEngine;
import org.palladiosimulator.metricspec.MetricDescription;
import org.palladiosimulator.metricspec.constants.MetricDescriptionConstants;

import spd.scalingtrigger.CPUUtilizationTrigger;
import spd.scalingtrigger.PointInTimeTrigger;
import spd.scalingtrigger.THRESHOLDDIRECTION;
import spd.scalingtrigger.util.ScalingtriggerSwitch;

public class ScalingTriggerInterpreter extends ScalingtriggerSwitch<ScalingTriggerPredicate> {

	private final SimulationEngine engine;
	private final TriggerContext context;

	public ScalingTriggerInterpreter(final SimulationEngine engine,
			final TriggerContext context) {
		this.engine = engine;
		this.context = context;
	}

	@Override
	public ScalingTriggerPredicate casePointInTimeTrigger(final PointInTimeTrigger object) {
		this.engine.scheduleEventAt(
				new PointInTimeTriggered(this.context, object.getPointInTime()),
				object.getPointInTime());
		return ScalingTriggerPredicate.ALWAYS;
	}

	@Override
	public ScalingTriggerPredicate caseCPUUtilizationTrigger(final CPUUtilizationTrigger object) {
		final double threshold = object.getThreshold();
		final double violationWindow = object.getViolationWindow();
		final THRESHOLDDIRECTION thresholdDirection = object.getThresholdDirection();

		return MeasuringPointTriggerContextMapper.instance().wrap(this.context, measurementMade -> {
			final SlingshotMeasuringValue value = measurementMade.getEntity();
			final MetricDescription metricDescription = value.getMetricDesciption();

			if (value.isCompatibleWith(MetricDescriptionConstants.RESOURCE_DEMAND_METRIC_TUPLE)) {
				// i.e. metricDescription == RESOURCE_DEMAND_METRIC_TUPLE
				final Measure<Double, Dimensionless> measurementValue = value.getMeasureForMetric(metricDescription);
				final double actualValue = measurementValue.doubleValue(Unit.ONE);

				// Either it should exceed or undercut, as specified in the thresholdDirection.
				return (thresholdDirection == THRESHOLDDIRECTION.EXCEDEED && actualValue > threshold + violationWindow)
						||
						(thresholdDirection == THRESHOLDDIRECTION.UNDERCUT
								&& actualValue < threshold - violationWindow);
			}

			return false; // default: Do not trigger.
		});
	}

}
