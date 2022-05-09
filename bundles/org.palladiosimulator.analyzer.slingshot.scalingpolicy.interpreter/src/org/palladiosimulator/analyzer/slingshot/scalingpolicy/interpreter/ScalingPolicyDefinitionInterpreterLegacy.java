package org.palladiosimulator.analyzer.slingshot.scalingpolicy.interpreter;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.AbstractTriggerEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.core.entities.SimulationInformation;

import spd.SPD;
import spd.ScalingPolicy;
import spd.util.SpdSwitch;

/**
 * This interpreter creates a {@link TriggerContext} and register it in the Monitor table.
 * 
 * @author Julijan Katic
 * @version 1.0
 *
 */
@Deprecated
public class ScalingPolicyDefinitionInterpreterLegacy extends SpdSwitch<Set<AbstractTriggerEvent>> {
	
	private final SimulationInformation simulationInformation;

	public ScalingPolicyDefinitionInterpreterLegacy(final SimulationInformation simulationInformation) {
		this.simulationInformation = simulationInformation;
	}

	@Override
	public Set<AbstractTriggerEvent> caseSPD(final SPD spd) {
		return spd.getScalingpolicy().stream()
				.flatMap(policy -> this.doSwitch(policy).stream())
				.collect(Collectors.toSet());
	}

	@Override
	public Set<AbstractTriggerEvent> caseScalingPolicy(final ScalingPolicy object) {
		final Set<AbstractTriggerEvent> result = new HashSet<>();

		final ScalingTriggerInterpreterLegacy scalingTriggerInterpreter = new ScalingTriggerInterpreterLegacy(object, simulationInformation);

		result.addAll(scalingTriggerInterpreter.doSwitch(object.getScalingtrigger()));

		return result;
	}

}
