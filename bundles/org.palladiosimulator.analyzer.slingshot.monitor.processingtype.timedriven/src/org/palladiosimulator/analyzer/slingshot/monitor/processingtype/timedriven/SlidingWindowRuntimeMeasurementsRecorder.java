package org.palladiosimulator.analyzer.slingshot.monitor.processingtype.timedriven;

import java.util.Arrays;

import javax.measure.Measure;
import javax.measure.quantity.Quantity;

import org.palladiosimulator.analyzer.slingshot.monitor.data.MeasurementMade;
import org.palladiosimulator.analyzer.slingshot.monitor.data.MeasurementUpdated;
import org.palladiosimulator.analyzer.slingshot.monitor.data.ProcessingTypeListener;
import org.palladiosimulator.analyzer.slingshot.simulation.api.SimulationScheduling;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.results.ResultEvent;
import org.palladiosimulator.edp2.models.measuringpoint.MeasuringPoint;
import org.palladiosimulator.edp2.util.MetricDescriptionUtility;
import org.palladiosimulator.measurementframework.MeasuringValue;
import org.palladiosimulator.metricspec.NumericalBaseMetricDescription;
import org.palladiosimulator.metricspec.constants.MetricDescriptionConstants;
import org.palladiosimulator.monitorrepository.ProcessingType;
import org.palladiosimulator.recorderframework.IRecorder;
import org.palladiosimulator.recorderframework.config.IRecorderConfiguration;

public final class SlidingWindowRuntimeMeasurementsRecorder extends ProcessingTypeListener implements IRecorder {

	private static final NumericalBaseMetricDescription POINT_IN_TIME_METRIC = MetricDescriptionConstants.POINT_IN_TIME_METRIC;
	private final SimulationScheduling scheduling;
	private final NumericalBaseMetricDescription dataMetric;

	protected SlidingWindowRuntimeMeasurementsRecorder(final ProcessingType processingType,
			final MeasuringPoint measuringPoint,
			final NumericalBaseMetricDescription metricDescription, final SimulationScheduling scheduling) {
		super(processingType, measuringPoint, metricDescription);
		this.scheduling = scheduling;
		this.dataMetric = this.getDataMetric();
	}

	@Override
	public void newMeasurementAvailable(final MeasuringValue newMeasurement) {
		final Measure<Double, Quantity> measure = newMeasurement.getMeasureForMetric(this.dataMetric);
		measure.doubleValue(this.dataMetric.getDefaultUnit());

		this.scheduling.scheduleForSimulation(new MeasurementMade(newMeasurement));
	}

	@Override
	public void flush() {
		// TODO Auto-generated method stub

	}

	@Override
	public void initialize(final IRecorderConfiguration arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeData(final MeasuringValue arg0) {
		this.newMeasurementAvailable(arg0);
	}

	@Override
	public ResultEvent<MeasurementUpdated> onMeasurementMade(final MeasurementMade measurementMade) {
		return ResultEvent.empty();
	}

	private NumericalBaseMetricDescription getDataMetric() {
		return Arrays.stream(MetricDescriptionUtility.toBaseMetricDescriptions(this.getDataMetric()))
				.filter(m -> !MetricDescriptionUtility.metricDescriptionIdsEqual(m, POINT_IN_TIME_METRIC))
				.findAny()
				.map(m -> (NumericalBaseMetricDescription) m)
				.orElse(POINT_IN_TIME_METRIC);
	}
}
