package org.palladiosimulator.analyzer.slingshot.simulation.resourcesimulation.impl.resources;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Hashtable;
import java.util.Set;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.simulation.core.events.SimulationStarted;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.results.ResultEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.resourcesimulation.events.JobFinished;
import org.palladiosimulator.analyzer.slingshot.simulation.resourcesimulation.events.JobInitiated;
import org.palladiosimulator.analyzer.slingshot.simulation.resourcesimulation.events.JobProgressed;
import org.palladiosimulator.analyzer.slingshot.simulation.resourcesimulation.events.JobScheduled;
import org.palladiosimulator.analyzer.slingshot.simulation.resourcesimulation.impl.Job;
import org.palladiosimulator.analyzer.slingshot.simulation.resourcesimulation.impl.ResourceSimulationImpl;
import org.palladiosimulator.analyzer.slingshot.simulation.systemsimulation.impl.events.RequestFinished;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.events.UserFinished;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.events.UserStarted;
import de.uka.ipd.sdq.probfunction.math.util.MathTools;

/**
 * An event-driven implemenation of ProcessorSharingResource where the behavior is as specified in 
 * de.uka.ipd.sdq.scheduler.resources.active.SimFCFSResource
 * 
 * @author Floriment Klinaku
 *
 */
public class FCFSResource implements IResource {
	private final Logger LOGGER = Logger.getLogger(ResourceSimulationImpl.class);

	// this is the data that is updated event after event for an activeresource.
	private final Deque<Job> processQ = new ArrayDeque<Job>();
	private final Hashtable<Job, Double> running_processes = new Hashtable<Job, Double>();
	private double last_time = 0.0;
	
	// all these should  belong to a registry
	// TODO:: the Resource Container ID is container in the Request and its base is the PCM spec.
	// so the extension creating the Request and putting as an event in the bus processes also the resource env to translate 
	// between the service request and the actual resource.
	
    // maps (ResourceContainer ID, ResourceType ID) -> SimActiveResource
	// private Map<String, SimActiveResource> containerToResourceMap;

	public FCFSResource() {

	}

	

	
	@Override
	public ResultEvent<DESEvent> onSimulationStarted(SimulationStarted evt) {
		// activate resouces
		// we could create a tuple of next processing finished and also that a user
		// request is finished.
		// new ProcessingFinished(delay) -> next
		// new UserFinished(now)
		return new ResultEvent<DESEvent>(Set.of());
	}


	@Override
	public ResultEvent<DESEvent> onJobInitiated(JobInitiated evt) {

		LOGGER.info(String.format("User requests processing '%f' users for closed workload simulation", evt.time()));

		toNow(evt.time());
		
		
		// TODO:: Demand should come from the clients currently all set to one.
		Job newJob = evt.getEntity();

		running_processes.put(newJob, newJob.getDemand());
		processQ.add(newJob);
		
		// add demand to the resource
		// new ProcessingStarted
		// it is the the one that arrived now
		if(processQ.size()==1) {
			LOGGER.info("[User Arrival]: Single user -> we need to schedule the getNextEvent");
			return new ResultEvent<DESEvent>(Set.of(new JobScheduled(newJob,0), getNextEvent()));
		} else { 
			LOGGER.info("[User Arrival]: Multiple users exist -> wait in queue");
			return new ResultEvent<DESEvent>(Set.of(new JobScheduled(newJob,0)));
		}
	}

	
	@Override
	public ResultEvent<DESEvent> onJobFinished(JobFinished jobFinishedEvt) {

			// the state of the resource has not changed until this point in time.
			toNow(jobFinishedEvt.time());
			LOGGER.info(String.format("[Processing Finished]: User requests finished at '%f'", jobFinishedEvt.time()));

			Job job = jobFinishedEvt.getEntity();
			
			assert MathTools.equalsDouble(0, running_processes.get(job)) : "Remaining demand ("
					+ running_processes.get(job) + ") not zero!";
			running_processes.remove(job);
			processQ.remove(job);
//	       fireStateChange(processQ.size(), 0); -> for this another type of events might be introduced
//	       fireDemandCompleted(first); -> UserFinished

			RequestFinished userFinished = new RequestFinished(job.getRequest());

//	       LoggingWrapper.log("Demand of Process " + first + " finished.");
//	       scheduleNextEvent();
//	       first.activate();

		// we could create a tuple of next processing finished and also that a user
		// request is finished.
		// new ProcessingFinished(delay) -> next
		// new UserFinished(now)
		return new ResultEvent<DESEvent>(Set.of(getNextEvent(), userFinished));
	}

	@Override
	public ResultEvent<DESEvent> onJobProgressed(JobProgressed evt) {

		return new ResultEvent<DESEvent>(Set.of());
	}
	
	private JobFinished getNextEvent() {
        final Job first = processQ.peek();
        // here we get rid of events that are scheduled on processing finished.
        // processingFinished.removeEvent(); -> no need to remove events that have been scheduled to the engine
        if (first != null) {
            final double time = running_processes.get(first);
//            processingFinished.schedule(first, time);
            return new JobFinished(first,time);
        }
        return null;
    }

	private void toNow(final double simulationTime) {
		final double now = simulationTime;
		final double passed_time = now - last_time;
		if (MathTools.less(0, passed_time)) {
			final Job first = processQ.peek();
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
