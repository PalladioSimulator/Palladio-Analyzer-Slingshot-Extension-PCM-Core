package org.palladiosimulator.analyzer.slingshot.simulation.badextension;

import java.util.HashSet;
import java.util.Set;

import org.palladiosimulator.analyzer.slingshot.simulation.core.SimulationBehaviourExtension;
import org.palladiosimulator.analyzer.slingshot.simulation.core.events.SimulationStarted;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.annotations.EventCardinality;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.annotations.OnEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.results.ResultEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.events.UserFinished;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.events.UserStarted;
import org.palladiosimulator.pcm.usagemodel.UsageModel;

import com.google.common.eventbus.Subscribe;

@OnEvent(eventType = SimulationStarted.class, outputEventType = UserStarted.class, cardinality = EventCardinality.MANY)
public class MyBadExtension implements SimulationBehaviourExtension {

	@Override
	public void init(UsageModel usageModel) {
		// TODO Auto-generated method stub

	}
	
	@Subscribe public ResultEvent<UserFinished> onSimulationStart(SimulationStarted evt) {
		Set<UserFinished> initialEvents = new HashSet<UserFinished>();
		initialEvents.add(new UserFinished(null));
		ResultEvent<UserFinished> initialUserStartedEvents = new ResultEvent<UserFinished>(initialEvents);
		return initialUserStartedEvents;
	}

}
