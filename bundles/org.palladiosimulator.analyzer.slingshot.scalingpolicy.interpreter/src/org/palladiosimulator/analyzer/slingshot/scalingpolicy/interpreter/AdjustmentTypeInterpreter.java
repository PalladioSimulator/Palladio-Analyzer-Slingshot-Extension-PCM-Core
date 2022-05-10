package org.palladiosimulator.analyzer.slingshot.scalingpolicy.interpreter;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.AdjustmentExecutor;
import org.palladiosimulator.analyzer.slingshot.scalingpolicy.interpreter.adjustment.AbsoluteAdjustmentExecutor;
import org.palladiosimulator.analyzer.slingshot.scalingpolicy.interpreter.adjustment.RelativeAdjustmentExecutor;
import org.palladiosimulator.analyzer.slingshot.scalingpolicy.interpreter.adjustment.StepAdjustmentExecutor;
import org.palladiosimulator.analyzer.slingshot.simulation.core.entities.SimulationInformation;
import org.palladiosimulator.monitorrepository.MonitorRepository;
import org.palladiosimulator.pcm.allocation.Allocation;

import spd.adjustmenttype.AbsoluteAdjustment;
import spd.adjustmenttype.RelativeAdjustment;
import spd.adjustmenttype.StepAdjustment;
import spd.adjustmenttype.util.AdjustmenttypeSwitch;

public class AdjustmentTypeInterpreter extends AdjustmenttypeSwitch<AdjustmentExecutor> {

	private static final Logger LOGGER = Logger.getLogger(AdjustmentTypeInterpreter.class);

	private final SimulationInformation information;
	private final Allocation allocation;
	private final MonitorRepository monitorRepository;

	public AdjustmentTypeInterpreter(final SimulationInformation simulationInformation,
			final Allocation allocation, final MonitorRepository monitorRepository) {
		this.information = simulationInformation;
		this.allocation = allocation;
		this.monitorRepository = monitorRepository;
	}

	@Override
	public AdjustmentExecutor caseStepAdjustment(final StepAdjustment object) {
		return new StepAdjustmentExecutor(object, this.information, this.allocation, this.monitorRepository);
	}

	@Override
	public AdjustmentExecutor caseAbsoluteAdjustment(final AbsoluteAdjustment object) {
		return new AbsoluteAdjustmentExecutor(object, this.information, this.allocation, this.monitorRepository);
	}

	@Override
	public AdjustmentExecutor caseRelativeAdjustment(final RelativeAdjustment object) {
		return new RelativeAdjustmentExecutor(object, this.information, this.allocation, this.monitorRepository);
	}

}
