package org.palladiosimulator.analyzer.slingshot.scalingpolicy.interpreter.adjustment;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.AdjustmentExecutor;
import org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.TriggerContext;
import org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.result.AdjustmentResult;
import org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.result.ModelChange;
import org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.result.ModelChangeAction;
import org.palladiosimulator.analyzer.slingshot.simulation.core.entities.SimulationInformation;
import org.palladiosimulator.commons.emfutils.EMFLoadHelper;
import org.palladiosimulator.edp2.models.measuringpoint.MeasuringPoint;
import org.palladiosimulator.monitorrepository.Monitor;
import org.palladiosimulator.monitorrepository.MonitorRepository;
import org.palladiosimulator.monitorrepository.MonitorRepositoryFactory;
import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.resourceenvironment.LinkingResource;
import org.palladiosimulator.pcm.resourceenvironment.ProcessingResourceSpecification;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;
import org.palladiosimulator.pcm.resourceenvironment.ResourceEnvironment;
import org.palladiosimulator.pcmmeasuringpoint.ActiveResourceMeasuringPoint;
import org.palladiosimulator.pcmmeasuringpoint.PcmmeasuringpointFactory;

import com.google.common.base.Preconditions;

import spd.adjustmenttype.AdjustmentType;

/**
 * This class also provides methods for copying resources containers, linking
 * resources, measuring points and assembly contexts.
 * 
 * The particular changes happen in the concrete
 * {@link #onTrigger(TriggerContext)}.
 * 
 * @author Julijan Katic
 *
 * @param <E> The actual adjustment type that is being used. Concrete classes
 *            should directly specify this.
 */
public abstract class AbstractAdjustmentExecutor<E extends AdjustmentType> implements AdjustmentExecutor {

	/** The actual model element */
	private final E adjustmentType;

	/** An object that contains the current simulation information. */
	private final SimulationInformation simulationInformation;

	/** The builder for the adjustment result. */
	private final AdjustmentResult.Builder adjustmentResultBuilder = AdjustmentResult.builder();

	/** The allocation model since it'll be manipulated as well. */
	private final Allocation allocation;

	/** The monitor repository model since it'll be manipulated as well. */
	private final MonitorRepository monitorRepository;

	@Deprecated
	AbstractAdjustmentExecutor(final E adjustmentType, final SimulationInformation simulationInformation) {
		this(adjustmentType, simulationInformation, null, null);
	}

	/**
	 * Constructs an adjustment executor.
	 * 
	 * The simulation information is needed for tracability, i.e. constructing
	 * {@link AdjustmentResult}. The allocation model is needed since new assembly
	 * contexts need to be created for each (new) resource containers. The monitor
	 * repository is needed to add new Monitors and MeasuringPoints to new copies,
	 * if the original were monitored.
	 * 
	 * @param adjustmentType        The model element for this executor.
	 * @param simulationInformation The current state of the simulation.
	 * @param allocation            The allocation model for adding new contexts.
	 * @param monitorRepository     The monitor model for adding new measuring
	 *                              points and corresponding monitors.
	 */
	AbstractAdjustmentExecutor(final E adjustmentType, final SimulationInformation simulationInformation,
			final Allocation allocation, final MonitorRepository monitorRepository) {
		this.adjustmentType = Objects.requireNonNull(adjustmentType);
		this.simulationInformation = Objects.requireNonNull(simulationInformation);
		this.allocation = allocation;
		this.monitorRepository = monitorRepository;
	}

	/**
	 * Returns the actual model element in SPD for the adjustment type.
	 * 
	 * @return The non-{@code null} model element.
	 */
	protected E getAdjustmentType() {
		return this.adjustmentType;
	}
	
	@Override
	public void modifyValues(final Map<String, Object> valuesToModify) {
		// Do nothing in standard case.
	}

	/**
	 * Copies all resource containers of one environment {@code times} times into
	 * the {@code destination}. This also includes copying of the
	 * {@link AssemblyContext}s and {@link MeasuringPoint}s that are inside the
	 * resource containers.
	 * 
	 * Each copy is traced inside the adjustment result.
	 * 
	 * @param environment The model element from where to copy the containers
	 * @param destination The destination of the copy action.
	 * @param times       A non-negative number telling how many times the copy
	 *                    action should be performed.
	 */
	protected void copyContainers(final ResourceEnvironment environment,
			final Collection<? super ResourceContainer> destination,
			final int times) {
		Preconditions.checkArgument(times >= 0, "Only a positive number is allowed.");
		final Collection<ResourceContainer> containers = environment.getResourceContainer_ResourceEnvironment();
		for (int i = 0; i < times; i++) {
			containers.forEach(container -> {
				final ResourceContainer copy = EcoreUtil.copy(container);
				copy.setId(EcoreUtil.generateUUID()); // Generate new ID, otherwise old ID will be copied as well
				this.connectToLinkingResources(environment.getLinkingResources__ResourceEnvironment(), container, copy);
				this.copyAllocationContexts(container, copy);
				this.copyMeasuringPoints(copy);
				destination.add(copy);
				this.adjustmentResultBuilder().addChange(ModelChange.builder()
						.withModelChangeAction(ModelChangeAction.ADDITION)
						.withModelElement(copy)
						.atSimulationTime(this.simulationInformation.currentSimulationTime())
						.build());
			});
		}
	}

	/**
	 * Copies all the measuring points and monitors that are present in this container.
	 * Note that each {@link Monitor} points to exactly one {@link MeasuringPoint}.
	 * The new copies are also present in the {@link MonitorRepository} and {@link MeasuringpointRepository},
	 * which are model files.
	 * 
	 * Currently, the only measuring points that are being copied are measuring points pointing
	 * to a {@link ProcessingResourceSpecification}.
	 * 
	 * @param container The copied container in which the new measuring points should be copied into.
	 * @see #copyMonitor(Monitor, MeasuringPoint)
	 * @see #copyActiveResourceSpec(ProcessingResourceSpecification, ResourceContainer, MeasuringPoint)
	 */
	protected void copyMeasuringPoints(final ResourceContainer container) {
		final List<Monitor> monitors = this.monitorRepository.getMonitors();
		for (final Monitor monitor : monitors) {
			final MeasuringPoint measuringPoint = monitor.getMeasuringPoint();
			final Optional<ProcessingResourceSpecification> spec = container
					.getActiveResourceSpecifications_ResourceContainer()
					.stream()
					.filter(s -> measuringPoint.getResourceURIRepresentation().equals(EMFLoadHelper.getResourceURI(s)))
					.findFirst();

			if (spec.isPresent()) {
				final MeasuringPoint copyMeasuringPoint = this.copyActiveResourceSpec(spec.get(), container,
						measuringPoint);
				this.copyMonitor(monitor, copyMeasuringPoint);
			}
		}
	}
	
	/**
	 * Copies the monitor into the {@link MonitorRepository} and connects it to {@link MeasuringPoint}.
	 * 
	 * @param monitor The monitor to copy.
	 * @param measuringPoint The measuring point to be linked by the monitor copy. This measuring point should already be a copy.
	 */
	protected void copyMonitor(final Monitor monitor, final MeasuringPoint measuringPoint) {
		final Monitor copyMonitor = MonitorRepositoryFactory.eINSTANCE.createMonitor();
		copyMonitor.setMeasuringPoint(measuringPoint);
		copyMonitor.setActivated(monitor.isActivated());
		copyMonitor.setId(EcoreUtil.generateUUID());
		copyMonitor.setEntityName(monitor.getEntityName()); // TODO: Maybe a better name for this?

		copyMonitor.setMonitorRepository(this.monitorRepository);
		this.monitorRepository.getMonitors().add(copyMonitor);

		copyMonitor.getMeasurementSpecifications().addAll(EcoreUtil.copyAll(monitor.getMeasurementSpecifications()));
		copyMonitor.getMeasurementSpecifications().forEach(spec -> spec.setMonitor(copyMonitor));

		this.adjustmentResultBuilder().addNewMonitor(copyMonitor);
	}

	/**
	 * Copies a {@link MeasuringPoint} that is pointing to an active resource
	 * specification of the {@link ResourceContainer}. The pointed
	 * {@link ProcessingResourceSpecification} is also copied and put into the new
	 * container.
	 * 
	 * @param spec           The spec to copy and put into the {@code copy}. The old
	 *                       spec is removed from {@code copy}.
	 * @param copy           The already copied container.
	 * @param measuringPoint The measuring point to copy and to point to the new
	 *                       copied spec.
	 * @return The copied measuring point.
	 */
	protected ActiveResourceMeasuringPoint copyActiveResourceSpec(final ProcessingResourceSpecification spec,
			final ResourceContainer copy,
			final MeasuringPoint measuringPoint) {
		assert copy.getActiveResourceSpecifications_ResourceContainer().contains(spec);

		final ProcessingResourceSpecification specCopy = EcoreUtil.copy(spec);
		specCopy.setId(EcoreUtil.generateUUID());

		copy.getActiveResourceSpecifications_ResourceContainer().remove(spec);
		copy.getActiveResourceSpecifications_ResourceContainer().add(specCopy);

		final ActiveResourceMeasuringPoint copyMeasuringPoint = PcmmeasuringpointFactory.eINSTANCE
				.createActiveResourceMeasuringPoint();
		copyMeasuringPoint.setActiveResource(specCopy);
		measuringPoint.getMeasuringPointRepository().getMeasuringPoints().add(copyMeasuringPoint);

		return copyMeasuringPoint;
	}

	/**
	 * Copies the allocation context that are inside the resource container. These
	 * new allocation contexts are traced in the adjustment result.
	 * 
	 * Note that allocation contexts are pointing to the resource container. The
	 * resource container themselves do not know by whom there a allocated.
	 * 
	 * @param originalContainer the original container from where to copy the
	 *                          allocation context.
	 * @param copy              The copied container in which the allocation
	 *                          contexts should be copied and put into.
	 */
	protected void copyAllocationContexts(final ResourceContainer originalContainer, final ResourceContainer copy) {
		if (this.allocation == null) {
			return;
		}

		this.allocation.getAllocationContexts_Allocation().stream()
				.filter(allocationContext -> allocationContext.getResourceContainer_AllocationContext().getId()
						.equals(originalContainer.getId()))
				.map(EcoreUtil::copy) // TODO: Should there be more done?
				.forEach(copiedContext -> {
					copiedContext.setResourceContainer_AllocationContext(copy);
					this.allocation.getAllocationContexts_Allocation().add(copiedContext);
					this.adjustmentResultBuilder().addNewAllocationContext(copiedContext);
				});
	}

	/**
	 * Connects the linking resources that are present in the collection and are
	 * linking {@code container} to the new {@code copy}.
	 * 
	 * @param linkingResources The linking resources to consider.
	 * @param container        The container to filter out linking resources that
	 *                         are not connecting to it.
	 * @param copy             The copied container to which the filtered linking
	 *                         resources should also be linking.
	 */
	protected void connectToLinkingResources(final Collection<? extends LinkingResource> linkingResources,
			final ResourceContainer container,
			final ResourceContainer copy) {
		linkingResources.stream()
				.map(LinkingResource::getConnectedResourceContainers_LinkingResource)
				.filter(containers -> containers.contains(container))
				.forEach(containers -> containers.add(copy));
	}

	/**
	 * Randomly deletes that containers in the environment {@code times} times. This
	 * will also delete the {@link AllocationContext}, {@code MeasuringPoint}s and
	 * {@link Monitor}s that are pointing to the deleted container. Furthermore,
	 * these deleted containers will also be removed from the linking resources.
	 * 
	 * Currently, the strategy to delete the containers are by randomly selecting
	 * them. In the future, there might be more strategies available.
	 * 
	 * @param environment The environment from where to delete the containers.
	 * @param containers  The containers to delete from the environment. // TODO:
	 *                    This is not needed.
	 * @param times       How many times to delete.
	 */
	protected void deleteContainers(final ResourceEnvironment environment,
			final Collection<ResourceContainer> containers,
			final int times) {
		final EList<ResourceContainer> resourceContainers = environment.getResourceContainer_ResourceEnvironment();
		for (int i = 0; i < times; i++) {
			final ResourceContainer randomResourceContainer = resourceContainers
					.get((int) (Math.random() * (resourceContainers.size() - 1)));
			this.deleteFromLinkingResources(environment, randomResourceContainer);
			this.deleteAllocationContexts(randomResourceContainer);
			this.deleteMeasuringPoints(randomResourceContainer);
			
			resourceContainers.remove(randomResourceContainer);

			this.adjustmentResultBuilder()
					.addChange(ModelChange.builder()
							.withModelChangeAction(ModelChangeAction.DELETION)
							.withModelElement(randomResourceContainer)
							.withOldSize(resourceContainers.size() + 1)
							.withNewSize(resourceContainers.size())
							.atSimulationTime(this.simulationInformation.currentSimulationTime())
							.build());
		}
	}

	/**
	 * Deletes the container from the linking resources it were.
	 * 
	 * @param environment The environment of linking resources.
	 * @param containerToDelete The container to delete from the linking resources.
	 */
	protected void deleteFromLinkingResources(final ResourceEnvironment environment,
			final ResourceContainer containerToDelete) {
		environment.getLinkingResources__ResourceEnvironment().stream()
			.filter(linkingResource -> linkingResource.getConnectedResourceContainers_LinkingResource().contains(containerToDelete))
			.forEach(linkingResource -> linkingResource.getConnectedResourceContainers_LinkingResource().remove(containerToDelete));
	}

	/**
	 * Deletes the allocation contexts that are pointing to the containers.
	 * 
	 * @param containerToDelete The container where the allocation contexts are pointing to should be deleted.
	 */
	protected void deleteAllocationContexts(final ResourceContainer containerToDelete) {
		this.allocation.getAllocationContexts_Allocation().stream()
			.filter(context -> context.getResourceContainer_AllocationContext().getId().equals(containerToDelete.getId()))
			.forEach(context -> {
				this.allocation.getAllocationContexts_Allocation().remove(context);
				// TODO: Mark this in adjustment result
			});
	}
	
	/**
	 * Deletes the measuring points and all monitors to the measuring points that point to something
	 * in the resource container.
	 * 
	 * @param containerToDelete
	 */
	protected void deleteMeasuringPoints(final ResourceContainer containerToDelete) {
		this.monitorRepository.getMonitors().forEach(monitor -> {
			final MeasuringPoint measuringPoint = monitor.getMeasuringPoint();
			containerToDelete.getActiveResourceSpecifications_ResourceContainer().stream()
				.filter(s -> measuringPoint.getResourceURIRepresentation().equals(EMFLoadHelper.getResourceURI(s)))
				.findAny()
				.ifPresent(s -> {
					measuringPoint.getMeasuringPointRepository().getMeasuringPoints().remove(measuringPoint);
					monitorRepository.getMonitors().remove(monitor);
					// TODO: Mark this in adjustment result
				});
		});
	}

	/**
	 * Returns the builder of the adjustment result.
	 * 
	 * @return A non-{@code null} builder to the adjustment result.
	 */
	protected AdjustmentResult.Builder adjustmentResultBuilder() {
		return this.adjustmentResultBuilder;
	}

	/**
	 * Convenience method to build the adjustment result.
	 * @return
	 */
	protected AdjustmentResult adjustmentResult() {
		return this.adjustmentResultBuilder.build();
	}

}
