package org.palladiosimulator.analyzer.slingshot.scalingpolicy.interpreter.adjustment;

import java.util.ArrayList;
import java.util.List;

import org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.TriggerContext;
import org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.result.AdjustmentResult;
import org.palladiosimulator.analyzer.slingshot.scalingpolicy.interpreter.TargetGroupTable;
import org.palladiosimulator.analyzer.slingshot.simulation.core.entities.SimulationInformation;
import org.palladiosimulator.monitorrepository.MonitorRepository;
import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;
import org.palladiosimulator.pcm.resourceenvironment.ResourceEnvironment;

import spd.adjustmenttype.StepAdjustment;
import spd.targetgroup.TargetGroup;

/**
 * Copies exactly the number of times specified in the
 * {@link StepAdjustment#getStepValue()} model element.
 * 
 * @author Julijan Katic
 *
 */
public final class StepAdjustmentExecutor extends AbstractAdjustmentExecutor<StepAdjustment> {

	public StepAdjustmentExecutor(final StepAdjustment adjustmentType,
			final SimulationInformation simulationInformation,
			final Allocation allocation, final MonitorRepository monitorRepository) {
		super(adjustmentType, simulationInformation, allocation, monitorRepository);
		// TODO Auto-generated constructor stub
	}

	@Override
	public AdjustmentResult onTrigger(final TriggerContext triggerContext) {
		this.adjustmentResultBuilder().withTriggerContext(triggerContext);

		final TargetGroup targetGroup = triggerContext.getTargetGroup();
		final ResourceEnvironment environment = TargetGroupTable.instance().getEnvironment(targetGroup);

		final List<ResourceContainer> newResourceContainers = new ArrayList<>(
				environment.getResourceContainer_ResourceEnvironment().size()
						* this.getAdjustmentType().getStepValue());

		this.copyContainers(environment, newResourceContainers, this.getAdjustmentType().getStepValue());

		environment.getResourceContainer_ResourceEnvironment().addAll(newResourceContainers);
		return this.adjustmentResult();
	}

}
