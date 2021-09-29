package org.palladiosimulator.analyzer.slingshot.monitor.processingtype.map;

import javax.measure.quantity.Quantity;
import javax.measure.unit.Unit;

import org.palladiosimulator.analyzer.slingshot.monitor.data.MeasurementMade;
import org.palladiosimulator.analyzer.slingshot.monitor.data.MeasurementUpdated;
import org.palladiosimulator.analyzer.slingshot.monitor.data.MeasurementUpdated.MeasurementUpdateInformation;
import org.palladiosimulator.analyzer.slingshot.monitor.data.ProcessingTypeListener;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.results.ResultEvent;
import org.palladiosimulator.edp2.util.MetricDescriptionUtility;
import org.palladiosimulator.measurementframework.MeasuringValue;
import org.palladiosimulator.metricspec.BaseMetricDescription;
import org.palladiosimulator.metricspec.MetricDescription;
import org.palladiosimulator.metricspec.MetricSpecPackage;
import org.palladiosimulator.metricspec.NumericalBaseMetricDescription;
import org.palladiosimulator.monitorrepository.map.Map;

public final class MapProcessingTypeRecorder extends ProcessingTypeListener {

	private final Map mapProcessingType;
	private final MetricDescription expectedInputMetric;
	private final NumericalBaseMetricDescription expectedOutputMetric;
	private final Unit<Quantity> defaultOutputUnit;
	private final boolean expectsBaseMetric;

	public MapProcessingTypeRecorder(final Map mapProcessingType) {
		super(mapProcessingType, mapProcessingType.getMeasurementSpecification().getMonitor().getMeasuringPoint(),
				mapProcessingType.getMeasurementSpecification().getMetricDescription());

		this.mapProcessingType = mapProcessingType;
		this.expectedInputMetric = mapProcessingType.getMeasurementSpecification().getMetricDescription();
		this.expectedOutputMetric = (NumericalBaseMetricDescription) this.mapProcessingType
				.getOutputMetricDescription();
		this.defaultOutputUnit = this.expectedOutputMetric.getDefaultUnit();
		this.expectsBaseMetric = MetricSpecPackage.Literals.BASE_METRIC_DESCRIPTION
				.isInstance(this.expectedInputMetric);
	}

	@Override
	public ResultEvent<MeasurementUpdated> onMeasurementMade(final MeasurementMade measurementMade) {
		final MeasuringValue newMeasurement = measurementMade.getEntity();
		if (newMeasurement.isCompatibleWith(this.expectedInputMetric)
				|| (this.expectsBaseMetric
						&& MetricDescriptionUtility.isBaseMetricDescriptionSubsumedByMetricDescription(
								(BaseMetricDescription) this.expectedInputMetric, this.expectedOutputMetric))) {
			final MeasuringValue transformedMeasurement = this.mapProcessingType.apply(newMeasurement);
			final MeasurementUpdateInformation information = new MeasurementUpdateInformation(transformedMeasurement,
					this.mapProcessingType, this.getMeasuringPoint());
			return ResultEvent.of(new MeasurementUpdated(information));
		} else {
			return ResultEvent.empty();
		}
	}

}
