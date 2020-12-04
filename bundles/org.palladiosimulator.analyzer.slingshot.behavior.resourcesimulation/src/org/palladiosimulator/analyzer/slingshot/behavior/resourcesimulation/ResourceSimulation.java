package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation;

import static org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.EventCardinality.SINGLE;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.repository.ResourceEnvironmentAccessor;
import org.palladiosimulator.analyzer.slingshot.simulation.core.events.SimulationFinished;
import org.palladiosimulator.analyzer.slingshot.simulation.core.events.SimulationStarted;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.SimulationBehaviorExtension;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.OnEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.results.ResultEvent;
import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.resourceenvironment.ProcessingResourceSpecification;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;

import com.google.common.eventbus.Subscribe;

/**
 * The resource simulation behavior initializes all the available resources on
 * start and will listen to requests for the simulation.
 * 
 * @author Julijan Katic
 */
@OnEvent(when = SimulationStarted.class, then = {})
@OnEvent(when = ActiveResourceRequested.class, then = { JobInitiated.class,
        ActiveResourceFinished.class }, cardinality = SINGLE)
@OnEvent(when = SimulationFinished.class, then = {})
@OnEvent(when = JobFinished.class, then = ActiveResourceFinished.class, cardinality = SINGLE)
public class ResourceSimulation implements SimulationBehaviorExtension {

	private final Logger LOGGER = Logger.getLogger(ResourceSimulation.class);

	private final Allocation allocation;
	private final ResourceEnvironmentAccessor resourceEnvironmentAccessor;

	/**
	 * Holds the context for each specification, as each processor works
	 * differently.
	 */
	private final Map<ProcessingResourceSpecification, JobContext<?>> jobContexts = new HashMap<>();

	@Inject
	public ResourceSimulation(final Allocation allocation) {
		this.allocation = allocation;
		this.resourceEnvironmentAccessor = new ResourceEnvironmentAccessor(allocation);
	}

	/**
	 * Initializes the resources from the resource environment model. Will always
	 * return no events.
	 */
	@Subscribe
	public ResultEvent<DESEvent> onSimulationStarted(final SimulationStarted evt) {
		return ResultEvent.empty();
	}

	/**
	 * Handles the request of a new job by looking at the resource environment,
	 * finding the right resource container and starting the processing of the job.
	 * <p>
	 * If the right resource container and specification can be found, and if the
	 * scheduling policy exists, then a {@link JobInitiated} event will be returned.
	 * Otherwise, {@link ActiveResourceFinished} will be directly returned.
	 */
	@Subscribe
	public ResultEvent<DESEvent> onActiveResourceRequested(final ActiveResourceRequested activeResourceRequested) {
		final ActiveResourceRequestContext jobRequested = activeResourceRequested.getEntity();
		final ResourceContainer resourceContainer = this.resourceEnvironmentAccessor
		        .findResourceContainerOfComponent(jobRequested.getComponent()).get();
		final ProcessingResourceSpecification prs = this.resourceEnvironmentAccessor
		        .findResourceSpecification(resourceContainer, jobRequested.getResourceType()).get();

		final Optional<JobContext<?>> jobContextOptional = this.getJobContext(prs);

		if (jobContextOptional.isPresent()) {
			final JobContext<?> jobContext = jobContextOptional.get();
			final Job job = new Job(jobRequested.getDemand(), prs.getActiveResourceType_ActiveResourceSpecification(),
			        jobRequested);

			return ResultEvent.of(new JobInitiated(jobContext, job, 0));
		} else {
			return ResultEvent.of(new ActiveResourceFinished(jobRequested, 0));
		}

	}

	/**
	 * This event handler will give a global response event that the certain request
	 * is finished.
	 * 
	 * @return Set containing {@link ActiveResourceFinished}.
	 */
	@Subscribe
	public ResultEvent<DESEvent> onJobFinished(final JobFinished evt) {
		return ResultEvent.of(new ActiveResourceFinished(evt.getEntity().getRequestContext(), 0));
	}

	/**
	 * Returns the corresponding JobContext instance for the right active resource.
	 * 
	 * @param spec The specification containing the scheduling policy.
	 * @return An instance of the job context for the right Scheduling Policy if
	 *         such a scheduling policy exists. Otherwise empty optional.
	 */
	private Optional<JobContext<?>> getJobContext(final ProcessingResourceSpecification spec) {
		/*
		 * TODO: Find a better way of initializing such contexts.
		 */
		JobContext<?> jobContext = this.jobContexts.get(spec);
		if (jobContext == null) {
			final String policyId = spec.getSchedulingPolicy().getId();
			if (policyId.equals("ProcessorSharing")) {
				jobContext = new ProcessorSharingJobContext(spec.getNumberOfReplicas(), spec,
				        spec.getResourceContainer_ProcessingResourceSpecification());
			} else if (policyId.equals("FCFS")) {
				jobContext = new FCFSJobContext(spec.getNumberOfReplicas(), spec,
				        spec.getResourceContainer_ProcessingResourceSpecification());
			}
		}

		if (jobContext != null) {
			this.jobContexts.put(spec, jobContext);
		}

		return Optional.ofNullable(jobContext);
	}

	/**
	 * Clears the contexts as soon as the simulation has finished.
	 * 
	 * @return an empty set.
	 */
	@Subscribe
	public ResultEvent<?> onSimulationFinished(final SimulationFinished simulationFinished) {
		jobContexts.clear();
		return ResultEvent.empty();
	}
}
