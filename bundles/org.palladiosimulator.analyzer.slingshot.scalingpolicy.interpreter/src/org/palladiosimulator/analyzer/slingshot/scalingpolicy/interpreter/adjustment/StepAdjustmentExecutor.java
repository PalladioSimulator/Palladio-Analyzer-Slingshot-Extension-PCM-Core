package org.palladiosimulator.analyzer.slingshot.scalingpolicy.interpreter.adjustment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
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

	private static final Logger LOGGER = Logger.getLogger(StepAdjustmentExecutor.class);
	
	private int stepValue;
	
	public StepAdjustmentExecutor(final StepAdjustment adjustmentType,
			final SimulationInformation simulationInformation,
			final Allocation allocation, final MonitorRepository monitorRepository) {
		super(adjustmentType, simulationInformation, allocation, monitorRepository);
		this.stepValue = this.getAdjustmentType().getStepValue();
	}

	@Override
	public AdjustmentResult onTrigger(final TriggerContext triggerContext) {
		this.adjustmentResultBuilder().withTriggerContext(triggerContext);

		final TargetGroup targetGroup = triggerContext.getTargetGroup();
		final ResourceEnvironment environment = TargetGroupTable.instance().getEnvironment(targetGroup);

		if (this.stepValue > 0) {
			final List<ResourceContainer> newResourceContainers = new ArrayList<>(
					environment.getResourceContainer_ResourceEnvironment().size()
							* this.stepValue);
	
			this.copyContainers(environment, newResourceContainers, this.stepValue);
			LOGGER.info("Copied!");
			environment.getResourceContainer_ResourceEnvironment().addAll(newResourceContainers);
		} else if (this.stepValue < 0) {
			this.deleteContainers(environment, null, -stepValue);
			LOGGER.info("Deleted!");
		}
		
		return this.adjustmentResult();
	}
	
	@Override
	public void modifyValues(final Map<String, Object> valuesToModify) {
		if (valuesToModify.containsKey("currentTargetGroupSize")) {
			final int currentTargetGroupSize = (Integer) valuesToModify.get("currentTargetGroupSize");
			
			if (valuesToModify.containsKey("maxTargetGroupSize")) {
				final int maxTargetGroupSize = (Integer) valuesToModify.get("maxTargetGroupSize");
				
				if (this.stepValue + currentTargetGroupSize > maxTargetGroupSize) {
					this.stepValue = maxTargetGroupSize - currentTargetGroupSize;
					// TODO: Trace this change.
				}
			}
			
			if (valuesToModify.containsKey("minTargetGroupSize")) {
				final int minTargetGroupSize = (Integer) valuesToModify.get("minTargetGroupSize");
				
				if (currentTargetGroupSize + this.stepValue < minTargetGroupSize) {
					this.stepValue = minTargetGroupSize - currentTargetGroupSize;
				}
			}
		}
	}
}
