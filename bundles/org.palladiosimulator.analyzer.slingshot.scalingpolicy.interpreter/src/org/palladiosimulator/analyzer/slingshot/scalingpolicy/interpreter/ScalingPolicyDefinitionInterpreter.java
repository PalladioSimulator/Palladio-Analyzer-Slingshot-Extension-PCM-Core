package org.palladiosimulator.analyzer.slingshot.scalingpolicy.interpreter;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.AbstractTriggerEvent;

import spd.SPD;
import spd.ScalingPolicy;
import spd.util.SpdSwitch;

public class ScalingPolicyDefinitionInterpreter extends SpdSwitch<Set<AbstractTriggerEvent>> {

	@Override
	public Set<AbstractTriggerEvent> caseSPD(final SPD spd) {
		return spd.getScalingpolicy().stream()
				.flatMap(policy -> this.doSwitch(policy).stream())
				.collect(Collectors.toSet());
	}

	@Override
	public Set<AbstractTriggerEvent> caseScalingPolicy(final ScalingPolicy object) {
		final Set<AbstractTriggerEvent> result = new HashSet<>();

		final ScalingTriggerInterpreter scalingTriggerInterpreter = new ScalingTriggerInterpreter(object);
		result.addAll(scalingTriggerInterpreter.doSwitch(object.getScalingtrigger()));

		return result;
	}

}
