package org.palladiosimulator.analyzer.slingshot.scalingpolicy.interpreter.adjustment;

import java.util.Collection;
import java.util.Objects;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.AdjustmentExecutor;
import org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.result.AdjustmentResult;
import org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.result.ModelChange;
import org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.result.ModelChangeAction;
import org.palladiosimulator.analyzer.slingshot.simulation.core.entities.SimulationInformation;
import org.palladiosimulator.pcm.resourceenvironment.LinkingResource;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;
import org.palladiosimulator.pcm.resourceenvironment.ResourceEnvironment;

import com.google.common.base.Preconditions;

import spd.adjustmenttype.AdjustmentType;
import spd.targetgroup.TargetGroup;

public abstract class AbstractAdjustmentExecutor<E extends AdjustmentType> implements AdjustmentExecutor {

	private final E adjustmentType;
	private final SimulationInformation simulationInformation;
	private AdjustmentResult.Builder adjustmentResultBuilder;
	
	AbstractAdjustmentExecutor(final E adjustmentType, final SimulationInformation simulationInformation) {
		this.adjustmentType = Objects.requireNonNull(adjustmentType);
		this.simulationInformation = Objects.requireNonNull(simulationInformation);
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
				final ResourceContainer copy = EcoreUtil.copy(container);
				copy.setId(EcoreUtil.generateUUID()); // Generate new ID, otherwise old ID will be copied as well
				connectToLinkingResources(environment.getLinkingResources__ResourceEnvironment(), container, copy);
				destination.add(copy);
				this.adjustmentResultBuilder().addChange(ModelChange.builder()
						.withModelChangeAction(ModelChangeAction.ADDITION)
						.withModelElement(copy)
						.atSimulationTime(simulationInformation.currentSimulationTime())
						.build());
			});
		}
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
	
//	@Override
//	public AdjustmentResult onTrigger(final TriggerContext triggerContext) {
//		final TargetGroup targetGroup = triggerContext.getTargetGroup();
//		this.environment = TargetGroupTable.instance().getEnvironment(targetGroup);
//
//		final List<ResourceContainer> newResourceContainers = new ArrayList<>(
//				environment.getResourceContainer_ResourceEnvironment().size() * this.adjustment.getStepValue());
//		
//		for (int i = 0; i < this.adjustment.getStepValue(); i++) {
//			final Map<ResourceContainer, ResourceContainer> copiedResourceContainer = this
//					.copyResourceContainers(environment.getResourceContainer_ResourceEnvironment());
//			
//			newResourceContainers.addAll(copiedResourceContainer.values());
//		}
//
//		environment.getResourceContainer_ResourceEnvironment().addAll(newResourceContainers);
//		return AdjustmentResult.EMPTY_RESULT; // TODO
//	}
//
//	private Map<ResourceContainer, ResourceContainer> copyResourceContainers(
//			final Collection<ResourceContainer> resourceContainers) {
//		final Map<ResourceContainer, ResourceContainer> result = new HashMap<>(resourceContainers.size());
//
//		resourceContainers.forEach(container -> {
//			final ResourceContainer copy = EcoreUtil.copy(container);
//			
//			// Set new ID, otherwise the old ID is re-used
//			copy.setId(EcoreUtil.generateUUID());
//			
//			// Add them to the respective linking resources.
//			copyToLinkingResources(container, copy);
//			
//			result.put(container, copy);
//		});
//
//		return result;
//	}
//
//	private void copyToLinkingResources(final ResourceContainer container, final ResourceContainer copy) {
//		environment.getLinkingResources__ResourceEnvironment().stream()
//			.map(linkingResource -> linkingResource.getConnectedResourceContainers_LinkingResource())	
//			.filter(connectedResources -> connectedResources.contains(container))
//			.forEach(connectedResources -> connectedResources.add(copy));
//	}
}
