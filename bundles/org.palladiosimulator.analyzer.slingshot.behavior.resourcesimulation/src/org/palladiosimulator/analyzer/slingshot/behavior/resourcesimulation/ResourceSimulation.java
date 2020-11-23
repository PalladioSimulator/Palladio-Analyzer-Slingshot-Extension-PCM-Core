package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.ActiveResourceRequestContext;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.jobs.FCFSJobContext;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.jobs.Job;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.jobs.JobContext;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.jobs.ProcessorSharingJobContext;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.ActiveResourceFinished;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.ActiveResourceRequested;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.JobFinished;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.JobInitiated;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.JobProgressed;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.repository.ResourceEnvironmentAccessor;
import org.palladiosimulator.analyzer.slingshot.simulation.core.events.SimulationStarted;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.SimulationBehaviorExtension;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.EventCardinality;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.OnEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.results.ResultEvent;
import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.resourceenvironment.ProcessingResourceSpecification;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;

import com.google.common.eventbus.Subscribe;

import de.uka.ipd.sdq.simucomframework.resources.SchedulingStrategy;

/**
 * The resource simulation behavior initializes all the available resources on
 * start and will listen to requests for the simulation.
 * 
 * @author Julijan Katic
 */
@OnEvent(when = SimulationStarted.class, then = {})
@OnEvent(when = JobProgressed.class, then = DESEvent.class, cardinality = EventCardinality.MANY)
@OnEvent(when = JobFinished.class, then = DESEvent.class, cardinality = EventCardinality.MANY)
@OnEvent(when = JobInitiated.class, then = DESEvent.class, cardinality = EventCardinality.MANY)
public class ResourceSimulation implements SimulationBehaviorExtension {

	private final Logger LOGGER = Logger.getLogger(ResourceSimulation.class);

	private final Allocation allocation;
	private final ResourceEnvironmentAccessor resourceEnvironmentAccessor;

	private final Map<ProcessingResourceSpecification, JobContext<?>> jobContexts = new HashMap<>();

	@Inject
	public ResourceSimulation(final Allocation allocation) {
		this.allocation = allocation;
		this.resourceEnvironmentAccessor = new ResourceEnvironmentAccessor(allocation);
	}

	/**
	 * Initializes the resources from the resource environment model.
	 */
	@Subscribe
	public ResultEvent<DESEvent> onSimulationStarted(final SimulationStarted evt) {
		return ResultEvent.empty();
	}

	/**
	 * Handles the request of a new job by looking at the resource environment,
	 * finding the right resource container and starting the processing of the job.
	 */
	@Subscribe
	public ResultEvent<DESEvent> onActiveResourceRequested(final ActiveResourceRequested activeResourceRequested) {
		final ActiveResourceRequestContext jobRequested = activeResourceRequested.getEntity();
		final ResourceContainer resourceContainer = this.resourceEnvironmentAccessor
		        .findResourceContainerOfComponent(jobRequested.getComponent()).get();
		final ProcessingResourceSpecification prs = this.resourceEnvironmentAccessor
		        .findResourceSpecification(resourceContainer, jobRequested.getResourceType()).get();

		final JobContext<?> jobContext = this.getJobContext(prs);
		final Job job = new Job(jobRequested.getDemand(), prs.getActiveResourceType_ActiveResourceSpecification(),
		        jobRequested);

		return ResultEvent.of(new JobInitiated(jobContext, job, 0));
	}

	/**
	 * This event handler will give a global response event that the certain request
	 * is finished.
	 * 
	 * @param evt
	 * @return
	 */
	@Subscribe
	public ResultEvent<DESEvent> onJobFinished(final JobFinished evt) {
		return ResultEvent.of(new ActiveResourceFinished(evt.getEntity().getRequestContext(), 0));
	}

	/**
	 * Returns the corresponding JobContext instance for the right active resource.
	 * 
	 * @param spec The specification containing the scheduling policy.
	 * @return A non-null instance of the job context for the according Scheduling
	 *         Policy.
	 */
	private JobContext<?> getJobContext(final ProcessingResourceSpecification spec) {
		JobContext<?> jobContext = this.jobContexts.get(spec);
		if (jobContext == null) {
			final String policyId = spec.getSchedulingPolicy().getId();
			if (policyId.equals(SchedulingStrategy.PROCESSOR_SHARING)) {
				jobContext = new ProcessorSharingJobContext(spec.getNumberOfReplicas(), spec,
				        spec.getResourceContainer_ProcessingResourceSpecification());
			} else if (policyId.equals(SchedulingStrategy.FCFS)) {
				jobContext = new FCFSJobContext(spec.getNumberOfReplicas(), spec,
				        spec.getResourceContainer_ProcessingResourceSpecification());
			}
		}

		return jobContext;
	}
}
