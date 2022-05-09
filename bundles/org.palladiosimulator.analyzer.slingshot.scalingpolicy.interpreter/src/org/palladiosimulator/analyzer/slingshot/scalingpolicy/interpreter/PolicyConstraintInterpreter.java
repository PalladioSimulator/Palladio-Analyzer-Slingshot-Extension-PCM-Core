package org.palladiosimulator.analyzer.slingshot.scalingpolicy.interpreter;

import org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.PolicyConstraintPredicate;
import org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.result.ConstraintResult;
import org.palladiosimulator.analyzer.slingshot.simulation.core.entities.SimulationInformation;
import org.palladiosimulator.pcm.resourceenvironment.ResourceEnvironment;

import spd.policyconstraint.CooldownConstraint;
import spd.policyconstraint.IntervallConstraint;
import spd.policyconstraint.TargetGroupSizeConstraint;
import spd.policyconstraint.ThrashingConstraint;
import spd.policyconstraint.util.PolicyconstraintSwitch;

public class PolicyConstraintInterpreter extends PolicyconstraintSwitch<PolicyConstraintPredicate> {
	
	private SimulationInformation information;

	@Override
	public PolicyConstraintPredicate caseTargetGroupSizeConstraint(final TargetGroupSizeConstraint object) {

		final int maxSize = object.getMaxSize();
		final int minSize = object.getMinSize();

		return triggerContext -> {
			final ResourceEnvironment environment = TargetGroupTable.instance()
					.getEnvironment(triggerContext.getTargetGroup());
			final int size = environment.getResourceContainer_ResourceEnvironment().size();

			return ConstraintResult.builder()
					.withConstraint(object)
					.withReason("Target Group exceeded", () -> size < maxSize)
					.withReason("Target Group too small", () -> size > minSize)
					.build();
		}; // TODO: Modifying constraints

	}

	@Override
	public PolicyConstraintPredicate caseIntervallConstraint(IntervallConstraint object) {
		final int duration = object.getIntervallDuration();
		final int offset = object.getOffset();
		
		return triggerContext -> ConstraintResult.builder(object)
					.withReason("Simulation time out of intervall.", () -> information.currentSimulationTime() < offset + duration)
					.build();
	
	}

	@Override
	public PolicyConstraintPredicate caseCooldownConstraint(CooldownConstraint object) {
		// TODO Auto-generated method stub
		return triggerContext -> {
			
			return ConstraintResult.builder(object)
					.build();
		};
	}

	@Override
	public PolicyConstraintPredicate caseThrashingConstraint(ThrashingConstraint object) {
		// TODO Auto-generated method stub
		return triggerContext -> {
			
			return ConstraintResult.builder(object)
					.build();
		};
	}
	
	

}
