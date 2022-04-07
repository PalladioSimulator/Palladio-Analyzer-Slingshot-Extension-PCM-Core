package org.palladiosimulator.analyzer.slingshot.scalingpolicy.interpreter;

import org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.result.ConstraintResult;
import org.palladiosimulator.pcm.resourceenvironment.ResourceEnvironment;

import spd.policyconstraint.TargetGroupSizeConstraint;
import spd.policyconstraint.util.PolicyconstraintSwitch;

public class PolicyConstraintInterpreter extends PolicyconstraintSwitch<PolicyConstraintPredicate> {

	@Override
	public PolicyConstraintPredicate caseTargetGroupSizeConstraint(TargetGroupSizeConstraint object) {
		
		final int maxSize = object.getMaxSize();
		final int minSize = object.getMinSize();
		
		return triggerContext -> {
			final ResourceEnvironment environment = TargetGroupTable.instance().getEnvironment(triggerContext.getTargetGroup());
			final int size = environment.getResourceContainer_ResourceEnvironment().size();
			
			return ConstraintResult.builder()
					.withConstraint(object)
					.withReason("Target Group exceeded", () -> size < maxSize)
					.withReason("Target Group too small", () -> size > minSize)
					.build();
			
		};
		
	}

	
	
}
