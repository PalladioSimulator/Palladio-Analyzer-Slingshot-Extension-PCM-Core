package org.palladiosimulator.analyzer.slingshot.scalingpolicy.interpreter;

import java.util.HashMap;
import java.util.Map;

import org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.PolicyConstraintPredicate;
import org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.result.ConstraintResult;
import org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.result.ConstraintResult.Modifier;
import org.palladiosimulator.analyzer.slingshot.simulation.core.entities.SimulationInformation;
import org.palladiosimulator.pcm.resourceenvironment.ResourceEnvironment;

import org.palladiosimulator.spd.constraints.AbstractConstraint;
import org.palladiosimulator.spd.constraints.policy.CooldownConstraint;
import org.palladiosimulator.spd.constraints.policy.IntervallConstraint;
import org.palladiosimulator.spd.constraints.policy.PolicyConstraint;
import org.palladiosimulator.spd.constraints.policy.ThrashingConstraint;
import org.palladiosimulator.spd.constraints.policy.util.PolicySwitch;
import org.palladiosimulator.spd.constraints.target.TargetConstraint;
import org.palladiosimulator.spd.constraints.target.TargetGroupSizeConstraint;
import org.palladiosimulator.spd.constraints.target.util.TargetSwitch;
import org.palladiosimulator.spd.constraints.util.ConstraintsSwitch;

public class ConstraintInterpreter extends ConstraintsSwitch<PolicyConstraintPredicate> {

	private SimulationInformation information;

	private final boolean modifyIfPossible;

	public ConstraintInterpreter(final boolean modifyIfPossible) {
		this.modifyIfPossible = modifyIfPossible;
	}

	public ConstraintInterpreter() {
		this(true);
	}

	@Override
	public PolicyConstraintPredicate caseAbstractConstraint(final AbstractConstraint object) {
		if (object instanceof TargetConstraint) {
			return (new TargetGroupConstraintInterpreter()).doSwitch(object);
		} else if (object instanceof PolicyConstraint) {
			return (new PolicyConstraintInterpreter()).doSwitch(object);
		}

		return super.caseAbstractConstraint(object); // TODO: Throw exception instead.
	}

	private final class PolicyConstraintInterpreter extends PolicySwitch<PolicyConstraintPredicate> {
		@Override
		public PolicyConstraintPredicate caseIntervallConstraint(final IntervallConstraint object) {
			final int duration = object.getIntervallDuration();
			final int offset = object.getOffset();

			return triggerContext -> ConstraintResult.builder(object)
					.withReason("Simulation time out of intervall.",
							() -> ConstraintInterpreter.this.information.currentSimulationTime() < offset + duration)
					.build();

		}

		@Override
		public PolicyConstraintPredicate caseCooldownConstraint(final CooldownConstraint object) {
			// TODO Auto-generated method stub
			return triggerContext -> ConstraintResult.builder(object)
					.build();
		}

		@Override
		public PolicyConstraintPredicate caseThrashingConstraint(final ThrashingConstraint object) {
			// TODO Auto-generated method stub
			return triggerContext -> ConstraintResult.builder(object)
					.build();
		}
	}

	private final class TargetGroupConstraintInterpreter extends TargetSwitch<PolicyConstraintPredicate> {
		@Override
		public PolicyConstraintPredicate caseTargetGroupSizeConstraint(final TargetGroupSizeConstraint object) {

			final int maxSize = object.getMaxSize();
			final int minSize = object.getMinSize();

			return triggerContext -> {
				final ResourceEnvironment environment = TargetGroupTable.instance()
						.getEnvironment(triggerContext.getTargetGroup());
				final int size = environment.getResourceContainer_ResourceEnvironment().size();

				// TODO: Export magic values to constants
				final Map<String, Object> modifyParameters = new HashMap<>();
				modifyParameters.put("currentTargetGroupSize", size);
				modifyParameters.put("maxTargetGroupSize", maxSize);
				modifyParameters.put("minTargetGroupSize", minSize);

				final Modifier modifier;

				if (ConstraintInterpreter.this.modifyIfPossible) {
					modifier = () -> triggerContext.getAdjustmentExecutor().modifyValues(modifyParameters);
				} else {
					modifier = null;
				}

				return ConstraintResult.builder()
						.withConstraint(object)
						.withModifiableReason("Target Group exceeded",
								() -> size < maxSize,
								modifier)
						.withModifiableReason("Target Group too small",
								() -> size > minSize,
								modifier)
						.build();
			};

		}
	}
}
