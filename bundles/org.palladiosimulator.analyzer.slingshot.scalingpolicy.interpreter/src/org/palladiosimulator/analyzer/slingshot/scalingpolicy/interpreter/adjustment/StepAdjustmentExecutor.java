package org.palladiosimulator.analyzer.slingshot.scalingpolicy.interpreter.adjustment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.AdjustmentExecutor;
import org.palladiosimulator.analyzer.slingshot.scalingpolicy.interpreter.TargetGroupTable;
import org.palladiosimulator.pcm.resourceenvironment.LinkingResource;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;
import org.palladiosimulator.pcm.resourceenvironment.ResourceEnvironment;

import spd.adjustmenttype.StepAdjustment;
import spd.targetgroup.TargetGroup;

public class StepAdjustmentExecutor implements AdjustmentExecutor {

	private final StepAdjustment adjustment;

	public StepAdjustmentExecutor(final StepAdjustment adjustment) {
		this.adjustment = adjustment;
	}

	@Override
	public void onTrigger(final TargetGroup targetGroup) {
		final ResourceEnvironment environment = TargetGroupTable.instance().getEnvironment(targetGroup);

		final List<ResourceContainer> newResourceContainers = new ArrayList<>(
				environment.getResourceContainer_ResourceEnvironment().size() * this.adjustment.getStepValue());
		final List<LinkingResource> newLinkingResources = new ArrayList<>(
				environment.getLinkingResources__ResourceEnvironment().size() * this.adjustment.getStepValue());

		for (int i = 0; i < this.adjustment.getStepValue(); i++) {
			final Map<ResourceContainer, ResourceContainer> copiedResourceContainer = this
					.copyResourceContainers(environment.getResourceContainer_ResourceEnvironment());
			final Collection<LinkingResource> copiedLinkingResources = this.copyLinkingResources(
					environment.getLinkingResources__ResourceEnvironment(), copiedResourceContainer);

			newResourceContainers.addAll(copiedResourceContainer.values());
			newLinkingResources.addAll(copiedLinkingResources);
		}

		environment.getLinkingResources__ResourceEnvironment().addAll(newLinkingResources);
		environment.getResourceContainer_ResourceEnvironment().addAll(newResourceContainers);
	}

	private Map<ResourceContainer, ResourceContainer> copyResourceContainers(
			final Collection<ResourceContainer> resourceContainers) {
		final Map<ResourceContainer, ResourceContainer> result = new HashMap<>(resourceContainers.size());

		resourceContainers.forEach(container -> {
			final ResourceContainer copy = EcoreUtil.copy(container);
			result.put(container, copy);
		});

		return result;
	}

	private Collection<LinkingResource> copyLinkingResources(
			final Collection<LinkingResource> linkingResources,
			final Map<ResourceContainer, ResourceContainer> resourceContainerCopies) {
		final List<LinkingResource> result = new ArrayList<>(linkingResources.size());

		for (final LinkingResource resource : linkingResources) {
			final LinkingResource copy = EcoreUtil.copy(resource);

			/* Add the copied resource containers to linking resource */
			copy.getConnectedResourceContainers_LinkingResource().clear();
			resource.getConnectedResourceContainers_LinkingResource().stream()
					.map(resourceContainerCopies::get)
					.forEach(copy.getConnectedResourceContainers_LinkingResource()::add);

			result.add(copy);
		}

		return result;
	}
}
