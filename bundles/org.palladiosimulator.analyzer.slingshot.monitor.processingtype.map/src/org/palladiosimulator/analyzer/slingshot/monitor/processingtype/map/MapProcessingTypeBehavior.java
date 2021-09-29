package org.palladiosimulator.analyzer.slingshot.monitor.processingtype.map;

import org.palladiosimulator.analyzer.slingshot.monitor.data.MonitorModelVisited;
import org.palladiosimulator.analyzer.slingshot.monitor.data.ProcessingTypeRevealed;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.SimulationBehaviorExtension;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.EventCardinality;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.OnEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.Reified;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.results.ResultEvent;
import org.palladiosimulator.monitorrepository.map.Map;

import com.google.common.eventbus.Subscribe;

@OnEvent(when = MonitorModelVisited.class, whenReified = Map.class, then = ProcessingTypeRevealed.class, cardinality = EventCardinality.SINGLE)
public class MapProcessingTypeBehavior implements SimulationBehaviorExtension {

	@Subscribe
	public ResultEvent<ProcessingTypeRevealed> onMapElementVisisted(
			@Reified(Map.class) final MonitorModelVisited<Map> mapElementVisited) {
		return ResultEvent.of(new ProcessingTypeRevealed(mapElementVisited.getEntity(),
				new MapProcessingTypeRecorder(mapElementVisited.getEntity())));
	}

}
