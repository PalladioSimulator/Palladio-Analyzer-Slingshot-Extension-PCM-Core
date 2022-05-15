package org.palladiosimulator.analyzer.slingshot.scalingpolicy.interpreter;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.AdjustmentExecutor;
import org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.ScalingTriggerPredicate;
import org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.TriggerContext;
import org.palladiosimulator.analyzer.slingshot.simulation.api.SimulationEngine;
import org.palladiosimulator.analyzer.slingshot.simulation.core.entities.SimulationInformation;
import org.palladiosimulator.monitorrepository.MonitorRepository;
import org.palladiosimulator.pcm.allocation.Allocation;

import spd.SPD;
import spd.ScalingPolicy;
import spd.adjustmenttype.AdjustmentType;
import spd.scalingtrigger.ScalingTrigger;
import spd.targetgroup.TargetGroup;
import spd.util.SpdSwitch;

/**
 * The scaling policy definition interpreter creates a {@link TriggerContext} for 
 * each scaling policy and registers it into a MonitorTargetGroupMapper.
 * 
 * @author Julijan Katic
 *
 */
public class ScalingPolicyDefinitionInterpreter extends SpdSwitch<List<TriggerContext>> {

	private final SimulationInformation information;
	private final Allocation allocation;
	private final MonitorRepository monitorRepository;
	private final MonitorTriggerMapper mapper;
	private final SimulationEngine engine;

	public ScalingPolicyDefinitionInterpreter(
			final SimulationInformation information,
			final Allocation allocation, 
			final MonitorRepository monitorRepository, 
			final MonitorTriggerMapper mapper,
			final SimulationEngine engine) {
		this.information = Objects.requireNonNull(information);
		this.allocation = allocation;
		this.monitorRepository = monitorRepository;
		this.mapper = mapper;
		this.engine = engine;
	}

	@Override
	public List<TriggerContext> caseSPD(final SPD spd) {
		return spd.getScalingpolicy().stream()
			.flatMap(policy -> this.doSwitch(policy).stream())
			.collect(Collectors.toList());
	}

	@Override
	public List<TriggerContext> caseScalingPolicy(final ScalingPolicy scalingPolicy) {
		final TriggerContext.Builder triggerContextBuilder = TriggerContext.builder();

		final AdjustmentTypeInterpreter adjustmentTypeInterpreter = new AdjustmentTypeInterpreter(this.information,
				this.allocation, this.monitorRepository);
		final AdjustmentExecutor adjustmentExecutor = adjustmentTypeInterpreter
				.doSwitch(scalingPolicy.getAdjustmenttype());

		final TriggerContext.Builder contextBuilder = triggerContextBuilder.withAdjustmentExecutor(adjustmentExecutor)
				.withAdjustmentType((AdjustmentType) scalingPolicy.getAdjustmenttype())
				.withTargetGroup((TargetGroup) scalingPolicy.getTargetgroup());
		
		final ScalingTriggerInterpreter scalingTriggerInterpreter = new ScalingTriggerInterpreter(this.engine, contextBuilder);
		final ScalingTriggerPredicate scalingTriggerPredicate = scalingTriggerInterpreter.doSwitch(scalingPolicy.getScalingtrigger());
		contextBuilder.withScalingTriggerPredicate(scalingTriggerPredicate)
					  .withScalingTrigger((ScalingTrigger) scalingPolicy.getScalingtrigger());
		
		final PolicyConstraintInterpreter policyConstraintInterpreter = new PolicyConstraintInterpreter();
		scalingPolicy.getPolicyconstraint().stream()
			.map(constraint -> policyConstraintInterpreter.doSwitch(constraint))
			.forEach(triggerContextBuilder::withConstraint);

		return List.of(contextBuilder.build());
	}

}
