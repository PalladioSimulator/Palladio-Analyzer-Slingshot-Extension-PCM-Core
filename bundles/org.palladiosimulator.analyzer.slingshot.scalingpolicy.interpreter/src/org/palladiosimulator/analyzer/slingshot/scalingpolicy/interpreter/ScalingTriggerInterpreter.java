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

import org.palladiosimulator.spd.triggers.CPUUtilizationTrigger;
import org.palladiosimulator.spd.triggers.PointInTimeTrigger;
import org.palladiosimulator.spd.triggers.THRESHOLDDIRECTION;
import org.palladiosimulator.spd.triggers.util.TriggersSwitch;

public class ScalingTriggerInterpreter extends TriggersSwitch<ScalingTriggerPredicate> {

	private final SimulationEngine engine;
	private final TriggerContext.Builder contextBuilder;

	public ScalingTriggerInterpreter(final SimulationEngine engine,
			final TriggerContext.Builder contextBuilder) {
		this.engine = engine;
		this.contextBuilder = contextBuilder;
	}

	@Override
	public ScalingTriggerPredicate casePointInTimeTrigger(final PointInTimeTrigger object) {
		this.contextBuilder.onBuild(context -> {
			this.engine.scheduleEventAt(new PointInTimeTriggered(context, object.getPointInTime()),
					object.getPointInTime());
		});
		return ScalingTriggerPredicate.ALWAYS;
	}

	@Override
	public ScalingTriggerPredicate caseCPUUtilizationTrigger(final CPUUtilizationTrigger object) {
		final double threshold = object.getThreshold();
		final double violationWindow = object.getViolationWindow();
		final THRESHOLDDIRECTION thresholdDirection = object.getThresholdDirection();

		return MeasuringPointTriggerContextMapper.instance().wrap((measurementMade, context) -> {
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
