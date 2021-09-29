package org.palladiosimulator.analyzer.slingshot.monitor.processingtype.feedthrough;

import org.palladiosimulator.analyzer.slingshot.monitor.data.MonitorModelVisited;
import org.palladiosimulator.analyzer.slingshot.monitor.data.ProcessingTypeRevealed;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.SimulationBehaviorExtension;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.EventCardinality;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.OnEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.Reified;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.results.ResultEvent;
import org.palladiosimulator.monitorrepository.FeedThrough;

import com.google.common.eventbus.Subscribe;

@OnEvent(when = MonitorModelVisited.class, whenReified = FeedThrough.class, then = ProcessingTypeRevealed.class, cardinality = EventCardinality.SINGLE)
public class FeedThroughRecorderBehavior implements SimulationBehaviorExtension {

	@Subscribe
	public ResultEvent<ProcessingTypeRevealed> onFeedThroughProcessingTypeVisited(
			@Reified(FeedThrough.class) final MonitorModelVisited<FeedThrough> feedThroughEvent) {
		return ResultEvent.of(new ProcessingTypeRevealed(feedThroughEvent.getEntity(),
				new FeedThroughRecorder(feedThroughEvent.getEntity())));
	}

}
