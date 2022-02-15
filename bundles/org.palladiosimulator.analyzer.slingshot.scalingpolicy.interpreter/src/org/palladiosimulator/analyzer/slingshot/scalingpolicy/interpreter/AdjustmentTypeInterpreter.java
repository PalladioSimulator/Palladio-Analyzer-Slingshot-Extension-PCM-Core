package org.palladiosimulator.analyzer.slingshot.scalingpolicy.interpreter;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.AdjustmentExecutor;
import org.palladiosimulator.analyzer.slingshot.scalingpolicy.interpreter.adjustment.StepAdjustmentExecutor;

import spd.adjustmenttype.StepAdjustment;
import spd.adjustmenttype.util.AdjustmenttypeSwitch;

public class AdjustmentTypeInterpreter extends AdjustmenttypeSwitch<AdjustmentExecutor> {

	private static final Logger LOGGER = Logger.getLogger(AdjustmentTypeInterpreter.class);

	@Override
	public AdjustmentExecutor caseStepAdjustment(final StepAdjustment object) {
		return new StepAdjustmentExecutor(object);
	}

}
