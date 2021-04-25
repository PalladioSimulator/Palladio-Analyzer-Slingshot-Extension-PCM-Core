package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation;

import static org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.EventCardinality.SINGLE;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.jobs.Job;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.resources.active.ActiveResource;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.resources.active.DelayResource;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.resources.active.FCFSResource;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.resources.active.ProcessorSharingResource;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.JobFinished;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.JobInitiated;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.repository.ResourceEnvironmentAccessor;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.resource.ResourceDemandRequest;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.ActiveResourceFinished;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.ResourceDemandRequested;
import org.palladiosimulator.analyzer.slingshot.simulation.core.events.SimulationFinished;
import org.palladiosimulator.analyzer.slingshot.simulation.core.events.SimulationStarted;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.SimulationBehaviorExtension;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.OnEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.results.ResultEvent;
import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.allocation.AllocationContext;
import org.palladiosimulator.pcm.core.PCMRandomVariable;
import org.palladiosimulator.pcm.resourceenvironment.ProcessingResourceSpecification;
import org.palladiosimulator.pcm.resourcetype.SchedulingPolicy;

import com.google.common.eventbus.Subscribe;

import de.uka.ipd.sdq.simucomframework.resources.SchedulingStrategy;
import de.uka.ipd.sdq.simucomframework.variables.StackContext;

/**
 * The resource simulation behavior initializes all the available resources on
 * start and will listen to requests for the simulation.
 * 
 * @author Julijan Katic
 */
@OnEvent(when = SimulationStarted.class, then = {})
@OnEvent(when = SimulationFinished.class, then = {})
@OnEvent(when = JobFinished.class, then = ActiveResourceFinished.class, cardinality = SINGLE)
public class ResourceSimulation implements SimulationBehaviorExtension {

	/*
	 * Scheduling Policy is a class and must hence be distinguished by its
	 * id.
	 */
	private static final String FCFS = "FCFS";
	private static final String PROCESSOR_SHARING = "ProcessorSharing";
	private static final String DELAY = "Delay";

	private final Logger LOGGER = Logger.getLogger(ResourceSimulation.class);

	private final Allocation allocation;
	private final ResourceEnvironmentAccessor resourceEnvironmentAccessor;

	private final Map<ProcessingResourceSpecification, ActiveResource> resources;

	@Inject
	public ResourceSimulation(final Allocation allocation) {
		this.allocation = allocation;
		this.resourceEnvironmentAccessor = new ResourceEnvironmentAccessor(allocation);
		this.resources = new HashMap<>();
	}

	@Override
	public void init() {
		this.initializeResourceModel();
	}

	/**
	 * Creates the map of each
	 */
	private void initializeResourceModel() {
		this.allocation.getAllocationContexts_Allocation().stream()
				.map(AllocationContext::getResourceContainer_AllocationContext)
				.flatMap(container -> container.getActiveResourceSpecifications_ResourceContainer().stream())
				.forEach(this::addResource);
	}

	private void addResource(final ProcessingResourceSpecification spec) {
		final int numberOfReplicas = spec.getNumberOfReplicas();
		final PCMRandomVariable processingRate = spec.getProcessingRate_ProcessingResourceSpecification();
		final SchedulingPolicy schedulingPolicy = spec.getSchedulingPolicy();

		ActiveResource resource = null;
		String resourceName;

		switch (schedulingPolicy.getId()) {
		case FCFS: {
			resourceName = SchedulingStrategy.FCFS.toString();
			resource = new FCFSResource(UUID.randomUUID().toString(), resourceName, numberOfReplicas);
			break;
		}
		case PROCESSOR_SHARING: {
			resourceName = SchedulingStrategy.PROCESSOR_SHARING.toString();
			resource = new ProcessorSharingResource(UUID.randomUUID().toString(), resourceName, numberOfReplicas);
			break;
		}
		case DELAY: {
			resourceName = SchedulingStrategy.DELAY.toString();
			resource = new DelayResource(UUID.randomUUID().toString(), resourceName);
		}

		default:
			throw new IllegalArgumentException("Unexpected value: " + schedulingPolicy.getId());
		}

		this.resources.put(spec, resource);
	}

	/**
	 * Initializes the resources from the resource environment model. Will always
	 * return no events.
	 */
	@Subscribe
	public ResultEvent<DESEvent> onSimulationStarted(final SimulationStarted evt) {
		return ResultEvent.empty();
	}

	@Subscribe
	public ResultEvent<DESEvent> onResourceDemandRequested(final ResourceDemandRequested resourceDemandRequested) {
		final ResourceDemandRequest request = resourceDemandRequested.getEntity();
//		final ResourceContainer resourceContainer = this.resourceEnvironmentAccessor
//				.findResourceContainerOfComponent(request.getAssemblyContext())
//				.orElseThrow();

		final double demand = StackContext.evaluateStatic(
				request.getParametricResourceDemand().getSpecification_ParametericResourceDemand().getSpecification(),
				Double.class, request.getUser().getStack().currentStackFrame());

		final Job job = Job.builder()
				.withDemand(demand)
				.withId(UUID.randomUUID().toString())
				.withProcessingResourceType(
						request.getParametricResourceDemand().getRequiredResource_ParametricResourceDemand())
				.withRequest(request)
				.build();

		return ResultEvent.of(new JobInitiated(job, 0));
	}

	/**
	 * This event handler will give a global response event that the certain request
	 * is finished.
	 * 
	 * @return Set containing {@link ActiveResourceFinished}.
	 */
	@Subscribe
	public ResultEvent<DESEvent> onJobFinished(final JobFinished evt) {
		return ResultEvent.of(new ActiveResourceFinished(evt.getEntity().getRequest(), 0));
	}

//	/**
//	 * Returns the corresponding JobContext instance for the right active resource.
//	 * 
//	 * @param spec The specification containing the scheduling policy.
//	 * @return An instance of the job context for the right Scheduling Policy if
//	 *         such a scheduling policy exists. Otherwise empty optional.
//	 */
//	private Optional<JobContext<?>> getJobContext(final ProcessingResourceSpecification spec) {
//		/*
//		 * TODO: Find a better way of initializing such contexts.
//		 */
//		JobContext<?> jobContext = this.jobContexts.get(spec);
//		if (jobContext == null) {
//			final String policyId = spec.getSchedulingPolicy().getId();
//			if (policyId.equals("ProcessorSharing")) {
//				jobContext = new ProcessorSharingJobContext(spec.getNumberOfReplicas(), spec,
//						spec.getResourceContainer_ProcessingResourceSpecification());
//			} else if (policyId.equals("FCFS")) {
//				jobContext = new FCFSJobContext(spec.getNumberOfReplicas(), spec,
//						spec.getResourceContainer_ProcessingResourceSpecification());
//			}
//		}
//
//		if (jobContext != null) {
//			this.jobContexts.put(spec, jobContext);
//		}
//
//		return Optional.ofNullable(jobContext);
//	}

	/**
	 * Clears the contexts as soon as the simulation has finished.
	 * 
	 * @return an empty set.
	 */
	@Subscribe
	public ResultEvent<?> onSimulationFinished(final SimulationFinished simulationFinished) {
		// this.jobContexts.clear();
		return ResultEvent.empty();
	}
}
