package org.palladiosimulator.analyzer.slingshot.monitor.processingtype.aggregator;

import org.palladiosimulator.analyzer.slingshot.monitor.data.MonitorModelVisited;
import org.palladiosimulator.analyzer.slingshot.monitor.data.ProcessingTypeRevealed;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.SimulationBehaviorExtension;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.EventCardinality;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.OnEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.Reified;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.results.ResultEvent;
import org.palladiosimulator.monitorrepository.FixedSizeAggregation;
import org.palladiosimulator.monitorrepository.VariableSizeAggregation;

import com.google.common.eventbus.Subscribe;

@OnEvent(when = MonitorModelVisited.class, whenReified = FixedSizeAggregation.class, then = ProcessingTypeRevealed.class, cardinality = EventCardinality.SINGLE)
@OnEvent(when = MonitorModelVisited.class, whenReified = VariableSizeAggregation.class, then = ProcessingTypeRevealed.class, cardinality = EventCardinality.SINGLE)
public class MeasurementsAggregatorBehavior implements SimulationBehaviorExtension {

	@Subscribe
	public ResultEvent<ProcessingTypeRevealed> onFixedSizeAggregationVisited(
			@Reified(FixedSizeAggregation.class) final MonitorModelVisited<FixedSizeAggregation> fixedSizeAggregationVisited) {
		return ResultEvent.of(new ProcessingTypeRevealed(fixedSizeAggregationVisited.getEntity(),
				new FixedSizeMeasurementsAggregator(fixedSizeAggregationVisited.getEntity())));
	}

	@Subscribe
	public ResultEvent<ProcessingTypeRevealed> onVariableSizeAggregationVisited(
			@Reified(VariableSizeAggregation.class) final MonitorModelVisited<VariableSizeAggregation> variableSizeAggregationVisited) {
		return ResultEvent.of(new ProcessingTypeRevealed(variableSizeAggregationVisited.getEntity(),
				new VariableSizeMeasurementAggregator(variableSizeAggregationVisited.getEntity())));
	}

}
