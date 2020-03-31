package org.palladiosimulator.analyzer.slingshot.simulation.resourcesimulation.impl;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Hashtable;
import java.util.Set;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.simulation.core.SimulationBehaviourExtension;
import org.palladiosimulator.analyzer.slingshot.simulation.core.events.SimulationStarted;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.annotations.EventCardinality;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.annotations.OnEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.results.ResultEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.resourcesimulation.events.ProcessingFinished;
import org.palladiosimulator.analyzer.slingshot.simulation.resourcesimulation.events.ProcessingStarted;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.events.UserFinished;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.events.UserStarted;
import org.palladiosimulator.pcm.usagemodel.UsageModel;

import com.google.common.eventbus.Subscribe;
import de.uka.ipd.sdq.probfunction.math.util.MathTools;

@OnEvent(eventType = ProcessingFinished.class, outputEventType = DESEvent.class, cardinality = EventCardinality.MANY)
@OnEvent(eventType = UserStarted.class, outputEventType = DESEvent.class, cardinality = EventCardinality.MANY)
public class ResourceSimulationImpl implements SimulationBehaviourExtension {

	private final Logger LOGGER = Logger.getLogger(ResourceSimulationImpl.class);

	
	private final Deque<ResourceProcess> processQ = new ArrayDeque<ResourceProcess>();
	private final Hashtable<ResourceProcess, Double> running_processes = new Hashtable<ResourceProcess, Double>();
	private double last_time = 0.0;

	public ResourceSimulationImpl() {

	}

	@Override
	public void init(UsageModel usageModel) {
		// TODO Auto-generated method stub
		LOGGER.info(String.format("Primitive FCFS Single ResourceSimulation initialized"));


	}

	@Subscribe
	public ResultEvent<DESEvent> onSimulationStarted(SimulationStarted evt) {
		// activate resouces
		// we could create a tuple of next processing finished and also that a user
		// request is finished.
		// new ProcessingFinished(delay) -> next
		// new UserFinished(now)
		return new ResultEvent<DESEvent>(Set.of());
	}

	// the event-driven variant of doProcessing: work arrives for processing
	@Subscribe
	public ResultEvent<DESEvent> onUserStarted(UserStarted evt) {

		LOGGER.info(String.format("User requests processing '%f' users for closed workload simulation", evt.time()));

		toNow(evt.time());
		
		
		// TODO:: Demand should come from the clients currently all set to one.
		ResourceProcess newProcess = new ResourceProcess(0, null, false, evt.getSimulatedUser(), 10.0);

		running_processes.put(newProcess, newProcess.getDemand());
		processQ.add(newProcess);
		
		// add demand to the resource
		// new ProcessingStarted
		// it is the the one that arrived now
		if(processQ.size()==1) {
			LOGGER.info("[User Arrival]: Single user -> we need to schedule the getNextEvent");
			return new ResultEvent<DESEvent>(Set.of(new ProcessingStarted(), getNextEvent()));
		} else { 
			LOGGER.info("[User Arrival]: Multiple users exist -> wait in queue");
			return new ResultEvent<DESEvent>(Set.of(new ProcessingStarted()));
		}
	}
	

	// the other event-driven trigger of the resource: work leaves
	@Subscribe
	public ResultEvent<DESEvent> onProcessingFinished(ProcessingFinished evt) {

			// the state of the resource has not changed until this point in time.
			toNow(evt.time());
			LOGGER.info(String.format("[Processing Finished]: User requests finished at '%f'", evt.time()));

			ResourceProcess process = evt.getProcess();
			
			assert MathTools.equalsDouble(0, running_processes.get(process)) : "Remaining demand ("
					+ running_processes.get(process) + ") not zero!";
			running_processes.remove(process);
			processQ.remove(process);
//	       fireStateChange(processQ.size(), 0); -> for this another type of events might be introduced
//	       fireDemandCompleted(first); -> UserFinished

			UserFinished userFinished = new UserFinished(evt.getProcess().getUser());

//	       LoggingWrapper.log("Demand of Process " + first + " finished.");
//	       scheduleNextEvent();
//	       first.activate();

		// we could create a tuple of next processing finished and also that a user
		// request is finished.
		// new ProcessingFinished(delay) -> next
		// new UserFinished(now)
		return new ResultEvent<DESEvent>(Set.of(getNextEvent(), userFinished));
	}

	public ProcessingFinished getNextEvent() {
        final ResourceProcess first = processQ.peek();
        // here we get rid of events that are scheduled on processing finished.
        // processingFinished.removeEvent(); -> no need to remove events that have been scheduled to the engine
        if (first != null) {
            final double time = running_processes.get(first);
//            processingFinished.schedule(first, time);
            return new ProcessingFinished(first,time);
        }
        return null;
    }

	private void toNow(final double simulationTime) {
		final double now = simulationTime;
		final double passed_time = now - last_time;
		if (MathTools.less(0, passed_time)) {
			final ResourceProcess first = processQ.peek();
			if (first != null) {
				double demand = running_processes.get(first);
				demand -= passed_time;

				// avoid trouble caused by rounding issues
				demand = MathTools.equalsDouble(demand, 0) ? 0.0 : demand;

				assert demand >= 0 : "Remaining demand (" + demand + ") smaller than zero!";

				running_processes.put(first, demand);
			}
		}
		last_time = now;
	}

}
