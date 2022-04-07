package org.palladiosimulator.analyzer.slingshot.scalingpolicy.interpreter;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.AdjustmentExecutor;
import org.palladiosimulator.analyzer.slingshot.scalingpolicy.interpreter.adjustment.AbsoluteAdjustmentExecutor;
import org.palladiosimulator.analyzer.slingshot.scalingpolicy.interpreter.adjustment.RelativeAdjustmentExecutor;
import org.palladiosimulator.analyzer.slingshot.scalingpolicy.interpreter.adjustment.StepAdjustmentExecutor;
import org.palladiosimulator.analyzer.slingshot.simulation.core.entities.SimulationInformation;

import spd.adjustmenttype.AbsoluteAdjustment;
import spd.adjustmenttype.RelativeAdjustment;
import spd.adjustmenttype.StepAdjustment;
import spd.adjustmenttype.util.AdjustmenttypeSwitch;

public class AdjustmentTypeInterpreter extends AdjustmenttypeSwitch<AdjustmentExecutor> {

	private static final Logger LOGGER = Logger.getLogger(AdjustmentTypeInterpreter.class);
	
	private final SimulationInformation information;
	
	public AdjustmentTypeInterpreter(final SimulationInformation simulationInformation) {
		this.information = simulationInformation;
	}

	@Override
	public AdjustmentExecutor caseStepAdjustment(final StepAdjustment object) {
		return new StepAdjustmentExecutor(object, information);
	}

	@Override
	public AdjustmentExecutor caseAbsoluteAdjustment(final AbsoluteAdjustment object) {
		return new AbsoluteAdjustmentExecutor(object, information);
	}
	
	@Override
	public AdjustmentExecutor caseRelativeAdjustment(final RelativeAdjustment object) {
		return new RelativeAdjustmentExecutor(object, information);
	}
	
}
