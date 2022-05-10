package org.palladiosimulator.analyzer.slingshot.scalingpolicy.interpreter;

import java.util.Objects;

import org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.AdjustmentExecutor;
import org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.TriggerContext;
import org.palladiosimulator.analyzer.slingshot.simulation.core.entities.SimulationInformation;
import org.palladiosimulator.monitorrepository.MonitorRepository;
import org.palladiosimulator.pcm.allocation.Allocation;

import spd.SPD;
import spd.ScalingPolicy;
import spd.adjustmenttype.AdjustmentType;
import spd.util.SpdSwitch;

/**
 * The scaling policy definition interpreter creates a {@link TriggerContext}
 * for each scaling policy and registers it into a MonitorTargetGroupMapper.
 * 
 * @author Julijan Katic
 *
 */
public class ScalingPolicyDefinitionInterpreter extends SpdSwitch<Void> {

	private final SimulationInformation information;
	private final Allocation allocation;
	private final MonitorRepository monitorRepository;

	public ScalingPolicyDefinitionInterpreter(
			final SimulationInformation information,
			final Allocation allocation, final MonitorRepository monitorRepository) {
		this.information = Objects.requireNonNull(information);
		this.allocation = allocation;
		this.monitorRepository = monitorRepository;
	}

	@Override
	public Void caseSPD(final SPD spd) {
		spd.getScalingpolicy().forEach(this::doSwitch);
		return super.caseSPD(spd);
	}

	@Override
	public Void caseScalingPolicy(final ScalingPolicy scalingPolicy) {
		final TriggerContext.Builder triggerContextBuilder = TriggerContext.builder();

		final AdjustmentTypeInterpreter adjustmentTypeInterpreter = new AdjustmentTypeInterpreter(this.information,
				this.allocation, this.monitorRepository);
		final AdjustmentExecutor adjustmentExecutor = adjustmentTypeInterpreter
				.doSwitch(scalingPolicy.getAdjustmenttype());

		triggerContextBuilder.withAdjustmentExecutor(adjustmentExecutor)
				.withAdjustmentType((AdjustmentType) scalingPolicy.getAdjustmenttype());

		return super.caseScalingPolicy(scalingPolicy);
	}

}
