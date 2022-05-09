package org.palladiosimulator.analyzer.slingshot.scalingpolicy.interpreter.adjustment;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.AdjustmentExecutor;
import org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.TriggerContext;
import org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.result.AdjustmentResult;
import org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.result.ModelChange;
import org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.result.ModelChangeAction;
import org.palladiosimulator.analyzer.slingshot.simulation.core.entities.SimulationInformation;
import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.resourceenvironment.LinkingResource;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;
import org.palladiosimulator.pcm.resourceenvironment.ResourceEnvironment;

import com.google.common.base.Preconditions;

import spd.adjustmenttype.AdjustmentType;

/**
 * This class also provides methods for copying resources containers, linking resources,
 * measuring points and assembly contexts.
 * 
 * The particular changes happen in the concrete {@link #onTrigger(TriggerContext)}.
 * 
 * @author Julijan Katic
 *
 * @param <E> The actual adjustment type that is being used. Concrete classes should directly specify this.
 */
public abstract class AbstractAdjustmentExecutor<E extends AdjustmentType> implements AdjustmentExecutor {

	private final E adjustmentType;
	private final SimulationInformation simulationInformation;
	private AdjustmentResult.Builder adjustmentResultBuilder;
	
	private final Optional<Allocation> allocation;
	
	AbstractAdjustmentExecutor(final E adjustmentType, final SimulationInformation simulationInformation) {
		this(adjustmentType, simulationInformation, null);
	}
	
	AbstractAdjustmentExecutor(final E adjustmentType, final SimulationInformation simulationInformation, final Allocation allocation) {
		this.adjustmentType = Objects.requireNonNull(adjustmentType);
		this.simulationInformation = Objects.requireNonNull(simulationInformation);
		this.allocation = Optional.ofNullable(allocation);
	}
	
	protected E getAdjustmentType() {
		return this.adjustmentType;
	}
	
	protected void copyContainers(final ResourceEnvironment environment,
								  final Collection<? super ResourceContainer> destination,
								  final int times) {
		Preconditions.checkArgument(times >= 0, "Only a positive number is allowed.");
		final Collection<ResourceContainer> containers = environment.getResourceContainer_ResourceEnvironment();
		for (int i = 0; i < times; i++) {
			containers.forEach(container -> {
				final ResourceContainer copy = EcoreUtil.copy(container); // TODO: Also copy assembly contexts
				copy.setId(EcoreUtil.generateUUID()); // Generate new ID, otherwise old ID will be copied as well
				connectToLinkingResources(environment.getLinkingResources__ResourceEnvironment(), container, copy);
				destination.add(copy);
				copyAssemblyContexts(copy);
				copyMeasuringPoints(copy);
				this.adjustmentResultBuilder().addChange(ModelChange.builder()
						.withModelChangeAction(ModelChangeAction.ADDITION)
						.withModelElement(copy)
						.atSimulationTime(simulationInformation.currentSimulationTime())
						.build());
			});
		}
	}
	
	protected void copyMeasuringPoints(final ResourceContainer container) {
		// TODO
	}
	
	protected void copyAssemblyContexts(final ResourceContainer container) {
		if (allocation.isEmpty()) {
			return;
		}
		
		final Allocation allocation = this.allocation.get();
		
		allocation.getAllocationContexts_Allocation().stream()
				.filter(allocationContext -> allocationContext.getResourceContainer_AllocationContext().getId().equals(container.getId()))
				.map(EcoreUtil::copy)
				.forEach(copiedContext -> {
					copiedContext.setResourceContainer_AllocationContext(container);
					allocation.getAllocationContexts_Allocation().add(copiedContext);
				});
	}
	
	protected void connectToLinkingResources(final Collection<? extends LinkingResource> linkingResources,
											 final ResourceContainer container, 
											 final ResourceContainer copy) {
		linkingResources.stream()
						.map(linkingResource -> linkingResource.getConnectedResourceContainers_LinkingResource())
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
