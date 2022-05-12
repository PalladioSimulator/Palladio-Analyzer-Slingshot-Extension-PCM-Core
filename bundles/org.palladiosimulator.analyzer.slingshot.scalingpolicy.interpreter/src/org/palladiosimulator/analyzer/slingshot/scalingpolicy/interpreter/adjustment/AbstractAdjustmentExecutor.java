package org.palladiosimulator.analyzer.slingshot.scalingpolicy.interpreter.adjustment;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
	private AdjustmentResult.Builder adjustmentResultBuilder;

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
				destination.add(copy);
				this.copyAllocationContexts(copy);
				this.copyMeasuringPoints(copy);
				this.adjustmentResultBuilder().addChange(ModelChange.builder()
						.withModelChangeAction(ModelChangeAction.ADDITION)
						.withModelElement(copy)
						.atSimulationTime(this.simulationInformation.currentSimulationTime())
						.build());
			});
		}
	}

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

	protected MeasuringPoint copyActiveResourceSpec(final ProcessingResourceSpecification spec,
			final ResourceContainer copy,
			final MeasuringPoint measuringPoint) {
		final ProcessingResourceSpecification specCopy = EcoreUtil.copy(spec);
		copy.getActiveResourceSpecifications_ResourceContainer().remove(spec);
		copy.getActiveResourceSpecifications_ResourceContainer().add(spec);

		final ActiveResourceMeasuringPoint copyMeasuringPoint = PcmmeasuringpointFactory.eINSTANCE
				.createActiveResourceMeasuringPoint();
		copyMeasuringPoint.setActiveResource(specCopy);
		measuringPoint.getMeasuringPointRepository().getMeasuringPoints().add(copyMeasuringPoint);

		return copyMeasuringPoint;
	}

	protected void copyAllocationContexts(final ResourceContainer container) {
		if (this.allocation == null) {
			return;
		}

		this.allocation.getAllocationContexts_Allocation().stream()
				.filter(allocationContext -> allocationContext.getResourceContainer_AllocationContext().getId()
						.equals(container.getId()))
				.map(EcoreUtil::copy)
				.forEach(copiedContext -> {
					copiedContext.setResourceContainer_AllocationContext(container);
					this.allocation.getAllocationContexts_Allocation().add(copiedContext);
					this.adjustmentResultBuilder().addNewAllocationContext(copiedContext);
				});
	}

	protected void connectToLinkingResources(final Collection<? extends LinkingResource> linkingResources,
			final ResourceContainer container,
			final ResourceContainer copy) {
		linkingResources.stream()
				.map(LinkingResource::getConnectedResourceContainers_LinkingResource)
				.filter(containers -> containers.contains(container))
				.forEach(containers -> containers.add(copy));
	}

	protected void deleteContainers(final ResourceEnvironment environment,
			final Collection<ResourceContainer> containers,
			final int times) {
		for (int i = 0; i < times; i++) {
			/* TODO: What ResourceContainer to select when deleting? */
		}
	}

	protected AdjustmentResult.Builder adjustmentResultBuilder() {
		if (this.adjustmentResultBuilder == null) {
			this.adjustmentResultBuilder = AdjustmentResult.builder();
		}
		return this.adjustmentResultBuilder;
	}

	protected AdjustmentResult adjustmentResult() {
		if (this.adjustmentResultBuilder == null) {
			throw new IllegalStateException("");
		}
		return this.adjustmentResultBuilder.build();
	}

}
