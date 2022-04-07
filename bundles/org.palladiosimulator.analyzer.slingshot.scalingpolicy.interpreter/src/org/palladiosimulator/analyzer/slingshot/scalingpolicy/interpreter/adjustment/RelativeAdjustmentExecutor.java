package org.palladiosimulator.analyzer.slingshot.scalingpolicy.interpreter.adjustment;

import java.util.Objects;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.AdjustmentExecutor;
import org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.TriggerContext;
import org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.result.AdjustmentResult;
import org.palladiosimulator.analyzer.slingshot.scalingpolicy.interpreter.TargetGroupTable;
import org.palladiosimulator.analyzer.slingshot.simulation.core.entities.SimulationInformation;
import org.palladiosimulator.pcm.resourceenvironment.ResourceEnvironment;

import spd.adjustmenttype.RelativeAdjustment;

/**
 * The relative adjustment adds a number of [TODO]
 * 
 * @author Julijan Katic
 *
 */
public final class RelativeAdjustmentExecutor extends AbstractAdjustmentExecutor<RelativeAdjustment> {
	
	private static final Logger LOGGER = Logger.getLogger(RelativeAdjustmentExecutor.class);
	
	public RelativeAdjustmentExecutor(final RelativeAdjustment adjustment, final SimulationInformation simulationInformation) {
		super(adjustment, simulationInformation);
	}
	
	@Override
	public AdjustmentResult onTrigger(final TriggerContext targetGroup) {
		this.adjustmentResultBuilder().withTriggerContext(targetGroup);
		final ResourceEnvironment environment = TargetGroupTable.instance().getEnvironment(targetGroup.getTargetGroup());
		
		final int relativeNumber = (int) Math.floor(environment.getResourceContainer_ResourceEnvironment().size() * getAdjustmentType().getPercentageValue());
		final int adding = Math.min(relativeNumber, getAdjustmentType().getMinAdjustmentValue());
		
		if (adding <= 0) {
			LOGGER.info("No new adjustment."); // TODO: Throw an exception that nothing changed?
			return AdjustmentResult.EMPTY_RESULT;
		}
		
		for (int i = 0; i < adding; i++) {
			
		}
		
		return adjustmentResult();
	}
	
	
}
