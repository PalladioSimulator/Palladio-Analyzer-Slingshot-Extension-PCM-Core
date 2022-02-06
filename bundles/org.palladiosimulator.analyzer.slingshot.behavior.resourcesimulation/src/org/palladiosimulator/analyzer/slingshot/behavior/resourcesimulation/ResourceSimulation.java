package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation;

import static org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.EventCardinality.SINGLE;

import java.util.Optional;
import java.util.UUID;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.jobs.Job;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.jobs.WaitingJob;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.AbstractJobEvent;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.JobFinished;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.JobInitiated;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.JobProgressed;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.repository.ResourceEnvironmentAccessor;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.resources.active.ActiveResource;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.resources.active.ActiveResourceCompoundKey;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.resources.active.ActiveResourceTable;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.resources.passive.PassiveResourceCompoundKey;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.resources.passive.PassiveResourceTable;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.resources.passive.SimplePassiveResource;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.resource.ResourceDemandRequest;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.resource.ResourceDemandRequest.ResourceType;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.ActiveResourceFinished;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.PassiveResourceAcquired;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.PassiveResourceReleased;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.ResourceDemandRequested;
import org.palladiosimulator.analyzer.slingshot.simulation.core.events.SimulationFinished;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.SimulationBehaviorExtension;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.EventCardinality;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.OnEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.results.ResultEvent;
import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.allocation.AllocationContext;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.repository.PassiveResource;

import com.google.common.eventbus.Subscribe;

import de.uka.ipd.sdq.simucomframework.variables.StackContext;

/**
 * The resource simulation behavior initializes all the available resources on
 * start and will listen to requests for the simulation.
 * 
 * @author Julijan Katic
 */
@OnEvent(when = SimulationFinished.class, then = {})
@OnEvent(when = JobInitiated.class, then = JobProgressed.class, cardinality = EventCardinality.MANY)
@OnEvent(when = JobFinished.class, then = ActiveResourceFinished.class, cardinality = SINGLE)
@OnEvent(when = JobProgressed.class, then = AbstractJobEvent.class, cardinality = EventCardinality.MANY)
@OnEvent(when = PassiveResourceReleased.class, then = PassiveResourceAcquired.class, cardinality = EventCardinality.MANY)
@OnEvent(when = ResourceDemandRequested.class, then = {
		JobInitiated.class, PassiveResourceAcquired.class
}, cardinality = SINGLE)
public class ResourceSimulation implements SimulationBehaviorExtension {

	private static final Logger LOGGER = Logger.getLogger(ResourceSimulation.class);

	private final Allocation allocation;
	private final ResourceEnvironmentAccessor resourceEnvironmentAccessor;

	private final ActiveResourceTable resourceTable;
	private final PassiveResourceTable passiveResourceTable;

	@Inject
	public ResourceSimulation(final Allocation allocation) {
		this.allocation = allocation;
		this.resourceEnvironmentAccessor = new ResourceEnvironmentAccessor(allocation);
		this.resourceTable = new ActiveResourceTable();
		this.passiveResourceTable = new PassiveResourceTable();
	}

	@Override
	public void init() {
		this.resourceTable.buildModel(this.allocation);
		this.passiveResourceTable.buildTable(this.allocation);
	}

	@Subscribe
	public ResultEvent<?> onResourceDemandRequested(final ResourceDemandRequested resourceDemandRequested) {
		final ResourceDemandRequest request = resourceDemandRequested.getEntity();

		if (request.getResourceType() == ResourceType.ACTIVE) {
			return this.initiateActiveResource(request);
		} else {
			return this.initiatePassiveResource(request);
		}
	}

	/**
	 * @param request
	 */
	private ResultEvent<PassiveResourceAcquired> initiatePassiveResource(final ResourceDemandRequest request) {
		final PassiveResource passiveResource = request.getPassiveResource().get();
		final AssemblyContext assemblyContext = request.getAssemblyContext();
		final Optional<SimplePassiveResource> passiveResourceInstance = this.passiveResourceTable
				.getPassiveResource(PassiveResourceCompoundKey.of(passiveResource, assemblyContext));

		if (passiveResourceInstance.isPresent()) {
			final WaitingJob waitingJob = this.createWaitingJob(request, passiveResource);

			return passiveResourceInstance.get().acquire(waitingJob);
		} else {
			return ResultEvent.empty();
		}
	}

	/**
	 * @param request
	 * @return
	 */
	private ResultEvent<JobInitiated> initiateActiveResource(final ResourceDemandRequest request) {
		final double demand = StackContext.evaluateStatic(
				request.getParametricResourceDemand().getSpecification_ParametericResourceDemand()
						.getSpecification(),
				Double.class, request.getUser().getStack().currentStackFrame());

		final AllocationContext context = this.resourceEnvironmentAccessor
				.findResourceContainerOfComponent(request.getAssemblyContext())
				.orElseThrow();

		final Job job = Job.builder()
				.withDemand(demand)
				.withId(UUID.randomUUID().toString())
				.withProcessingResourceType(
						request.getParametricResourceDemand().getRequiredResource_ParametricResourceDemand())
				.withRequest(request)
				.withAllocationContext(context)
				.build();

		return ResultEvent.of(new JobInitiated(job, 0));
	}

	/**
	 * @param request
	 * @param passiveResource
	 * @return
	 */
	private WaitingJob createWaitingJob(final ResourceDemandRequest request, final PassiveResource passiveResource) {
		final long demand = StackContext.evaluateStatic(
				request.getParametricResourceDemand().getSpecification_ParametericResourceDemand()
						.getSpecification(),
				Long.class, request.getUser().getStack().currentStackFrame());

		final WaitingJob waitingJob = WaitingJob.builder()
				.withPassiveResource(passiveResource)
				.withRequest(request)
				.withDemand(demand)
				.build();
		return waitingJob;
	}

	@Subscribe
	public ResultEvent<JobProgressed> onJobInitiated(final JobInitiated jobInitiated) {
		final Job job = jobInitiated.getEntity();
		final ActiveResourceCompoundKey id = new ActiveResourceCompoundKey(
				job.getAllocationContext().getResourceContainer_AllocationContext(), job.getProcessingResourceType());

		final Optional<ActiveResource> activeResource = this.resourceTable.getActiveResource(id);

		if (activeResource.isEmpty()) {
			LOGGER.error("No such active resource found!");
			return ResultEvent.empty();
		}

		return activeResource.get().onJobInitiated(jobInitiated);
	}

	@Subscribe
	public ResultEvent<PassiveResourceAcquired> onPassiveResourceReleased(
			final PassiveResourceReleased passiveResourceReleased) {
		final ResourceDemandRequest entity = passiveResourceReleased.getEntity();
		final Optional<SimplePassiveResource> passiveResource = this.passiveResourceTable.getPassiveResource(
				PassiveResourceCompoundKey.of(entity.getPassiveResource().get(), entity.getAssemblyContext()));

		if (passiveResource.isEmpty()) {
			LOGGER.error("No such passive resource found!");
			return ResultEvent.empty();
		}

		final WaitingJob waitingJob = this.createWaitingJob(entity, entity.getPassiveResource().get());
		return passiveResource.get().release(waitingJob);
	}

	@Subscribe
	public ResultEvent<? extends AbstractJobEvent> onJobProgressed(final JobProgressed jobProgressed) {
		final Job job = jobProgressed.getEntity();
		final ActiveResourceCompoundKey id = ActiveResourceCompoundKey.of(
				job.getAllocationContext().getResourceContainer_AllocationContext(), job.getProcessingResourceType());

		final Optional<ActiveResource> activeResource = this.resourceTable.getActiveResource(id);

		if (activeResource.isEmpty()) {
			LOGGER.error("No such resource found!");
			return ResultEvent.empty();
		}

		return activeResource.get().onJobProgressed(jobProgressed);
	}

	/**
	 * This event handler will give a global response event that the certain request
	 * is finished.
	 * 
	 * @return Set containing {@link ActiveResourceFinished}.
	 */
	@Subscribe
	public ResultEvent<ActiveResourceFinished> onJobFinished(final JobFinished evt) {
		return ResultEvent.of(new ActiveResourceFinished(evt.getEntity().getRequest(), 0));
	}

	/**
	 * Clears the contexts as soon as the simulation has finished.
	 * 
	 * @return an empty set.
	 */
	@Subscribe
	public ResultEvent<?> onSimulationFinished(final SimulationFinished simulationFinished) {
		this.resourceTable.clearResourcesFromJobs();
		this.passiveResourceTable.clearResourcesFromJobs();
		return ResultEvent.empty();
	}
}
