package org.palladiosimulator.analyzer.slingshot.monitor.processingtype.feedthrough;

import org.palladiosimulator.analyzer.slingshot.monitor.data.MeasurementMade;
import org.palladiosimulator.analyzer.slingshot.monitor.data.MeasurementUpdated;
import org.palladiosimulator.analyzer.slingshot.monitor.data.MeasurementUpdated.MeasurementUpdateInformation;
import org.palladiosimulator.analyzer.slingshot.monitor.data.ProcessingTypeListener;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.results.ResultEvent;
import org.palladiosimulator.edp2.models.measuringpoint.MeasuringPoint;
import org.palladiosimulator.metricspec.MetricDescription;
import org.palladiosimulator.monitorrepository.FeedThrough;

public final class FeedThroughRecorder extends ProcessingTypeListener {

	public FeedThroughRecorder(
			final FeedThrough processingType,
			final MeasuringPoint measuringPoint,
			final MetricDescription metricDescription) {
		super(processingType, measuringPoint, metricDescription);
	}

	public FeedThroughRecorder(final FeedThrough feedThrough) {
		super(feedThrough,
				feedThrough.getMeasurementSpecification().getMonitor().getMeasuringPoint(),
				feedThrough.getMeasurementSpecification().getMetricDescription());
	}

	@Override
	public ResultEvent<MeasurementUpdated> onMeasurementMade(final MeasurementMade measurementMade) {
		return ResultEvent.of(new MeasurementUpdated(
				new MeasurementUpdateInformation(measurementMade.getEntity(), this.getProcessingType(),
						this.getMeasuringPoint())));
	}

}
