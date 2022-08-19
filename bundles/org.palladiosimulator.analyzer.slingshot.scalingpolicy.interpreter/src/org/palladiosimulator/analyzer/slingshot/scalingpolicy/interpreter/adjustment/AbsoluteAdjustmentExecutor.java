package org.palladiosimulator.analyzer.slingshot.scalingpolicy.interpreter.adjustment;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.TriggerContext;
import org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.result.AdjustmentResult;
import org.palladiosimulator.analyzer.slingshot.scalingpolicy.interpreter.TargetGroupTable;
import org.palladiosimulator.analyzer.slingshot.simulation.core.entities.SimulationInformation;
import org.palladiosimulator.monitorrepository.MonitorRepository;
import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;
import org.palladiosimulator.pcm.resourceenvironment.ResourceEnvironment;

import org.palladiosimulator.spd.adjustments.AbsoluteAdjustment;
import org.palladiosimulator.spd.targets.TargetGroup;

/**
 * Adjusts absolutely to a certain goal.
 * 
 * @author Julijan Katic
 *
 */
public final class AbsoluteAdjustmentExecutor extends AbstractAdjustmentExecutor<AbsoluteAdjustment> {

	private static final Logger LOGGER = Logger.getLogger(AbsoluteAdjustmentExecutor.class);

	public AbsoluteAdjustmentExecutor(final AbsoluteAdjustment adjustmentType,
			final SimulationInformation simulationInformation,
			final Allocation allocation, final MonitorRepository monitorRepository) {
		super(adjustmentType, simulationInformation, allocation, monitorRepository);
		// TODO Auto-generated constructor stub
	}

	@Override
	public AdjustmentResult onTrigger(final TriggerContext triggerContext) {
		this.adjustmentResultBuilder().withTriggerContext(triggerContext);
		final TargetGroup targetGroup = triggerContext.getTargetGroup();
		final ResourceEnvironment resourceEnvironment = TargetGroupTable.instance().getEnvironment(targetGroup);

		final int goal = this.getAdjustmentType().getGoalValue();
		final int currentSize = resourceEnvironment.getResourceContainer_ResourceEnvironment().size();

		if (goal == currentSize) {
			LOGGER.info("Goal value has already been achieved.");
			return AdjustmentResult.NO_TRIGGER;
		} else if (goal > currentSize) {
			final int delta = goal - currentSize;
			this.increase(resourceEnvironment, delta);
		} else {
			final int delta = currentSize - goal;
			this.decrease(resourceEnvironment, delta);
		}

		return this.adjustmentResult();
	}

	private void increase(final ResourceEnvironment resourceEnvironment, final int delta) {
		assert delta > 0;
		LOGGER.info("Increase size by copying " + delta + " times.");

		final List<ResourceContainer> newResourceContainers = new ArrayList<>(
				resourceEnvironment.getResourceContainer_ResourceEnvironment().size() * delta);
		this.copyContainers(resourceEnvironment, newResourceContainers, delta);

		resourceEnvironment.getResourceContainer_ResourceEnvironment().addAll(newResourceContainers);
	}

	private void decrease(final ResourceEnvironment resourceEnvironment, final int delta) {
		assert delta > 0;
		LOGGER.info("Decrease size by deleting " + delta + " containers.");
		this.deleteContainers(resourceEnvironment, delta);
	}
}
