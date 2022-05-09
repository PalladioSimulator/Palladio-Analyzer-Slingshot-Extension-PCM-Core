package org.palladiosimulator.analyzer.slingshot.scalingpolicy.interpreter;

import java.util.Objects;

import org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.TriggerContext;
import org.palladiosimulator.analyzer.slingshot.simulation.core.entities.SimulationInformation;

import spd.SPD;
import spd.ScalingTrigger;
import spd.util.SpdSwitch;

/**
 * The scaling policy definition interpreter creates a {@link TriggerContext} for each scaling policy
 * and registers it into a MonitorTargetGroupMapper.
 * 
 * @author Julijan Katic
 *
 */
public class ScalingPolicyDefinitionInterpreter extends SpdSwitch<Void> {
	
	private final SimulationInformation information;
	
	public ScalingPolicyDefinitionInterpreter(final SimulationInformation information) {
		this.information = Objects.requireNonNull(information);
	}
	
	@Override
	public Void caseSPD(final SPD spd) {
		spd.getScalingpolicy().forEach(this::doSwitch);
		return super.caseSPD(spd);
	}

	@Override
	public Void caseScalingTrigger(final ScalingTrigger scalingTrigger) {
		final TriggerContext.Builder triggerContextBuilder = TriggerContext.builder();
		
		
		return super.caseScalingTrigger(scalingTrigger);
	}
	
	
}
