package org.palladiosimulator.analyzer.slingshot.scalingpolicy.interpreter;

import java.util.Set;

import org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.AbstractTriggerEvent;
import org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.AdjustmentExecutor;
import org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.PointInTimeTriggered;
import org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.TriggerContext;
import org.palladiosimulator.analyzer.slingshot.simulation.core.entities.SimulationInformation;

import spd.ScalingPolicy;
import spd.scalingtrigger.CPUUtilizationTrigger;
import spd.scalingtrigger.HDDUtilizationTrigger;
import spd.scalingtrigger.IdleTimeTrigger;
import spd.scalingtrigger.NetworkUtilizationTrigger;
import spd.scalingtrigger.PointInTimeTrigger;
import spd.scalingtrigger.ProcessingResourceUtilizationBasedTrigger;
import spd.scalingtrigger.RAMUtilizationTrigger;
import spd.scalingtrigger.ResourceUtilizationBasedTrigger;
import spd.scalingtrigger.ResponseTimeTrigger;
import spd.scalingtrigger.TaskCountTrigger;
import spd.scalingtrigger.util.ScalingtriggerSwitch;
import spd.targetgroup.TargetGroup;

public class ScalingTriggerInterpreter extends ScalingtriggerSwitch<Set<AbstractTriggerEvent>> {

	private final ScalingPolicy parent;
	private final SimulationInformation information;

	private final AdjustmentTypeInterpreter adjustmentTypeInterpreter;

	public ScalingTriggerInterpreter(final ScalingPolicy parent, final SimulationInformation information) {
		this.parent = parent;
		this.information = information;

		this.adjustmentTypeInterpreter = new AdjustmentTypeInterpreter(information);
	}

	@Override
	public Set<AbstractTriggerEvent> casePointInTimeTrigger(final PointInTimeTrigger object) {
		final AdjustmentExecutor executor = this.adjustmentTypeInterpreter.doSwitch(this.parent.getAdjustmenttype());
		final TriggerContext context = TriggerContext.builder()
				.withAdjustmentExecutor(executor)
				.withTargetGroup((TargetGroup) this.parent.getTargetgroup())
				.build();

		return Set.of(new PointInTimeTriggered(context, object.getPointInTime()));
	}

	@Override
	public Set<AbstractTriggerEvent> caseCPUUtilizationTrigger(final CPUUtilizationTrigger object) {
		// TODO Auto-generated method stub
		return super.caseCPUUtilizationTrigger(object);
	}

	@Override
	public Set<AbstractTriggerEvent> caseRAMUtilizationTrigger(final RAMUtilizationTrigger object) {
		// TODO Auto-generated method stub
		return super.caseRAMUtilizationTrigger(object);
	}

	@Override
	public Set<AbstractTriggerEvent> caseHDDUtilizationTrigger(final HDDUtilizationTrigger object) {
		// TODO Auto-generated method stub
		return super.caseHDDUtilizationTrigger(object);
	}

	@Override
	public Set<AbstractTriggerEvent> caseIdleTimeTrigger(final IdleTimeTrigger object) {
		// TODO Auto-generated method stub
		return super.caseIdleTimeTrigger(object);
	}

	@Override
	public Set<AbstractTriggerEvent> caseTaskCountTrigger(final TaskCountTrigger object) {
		// TODO Auto-generated method stub
		return super.caseTaskCountTrigger(object);
	}

	@Override
	public Set<AbstractTriggerEvent> caseNetworkUtilizationTrigger(final NetworkUtilizationTrigger object) {
		// TODO Auto-generated method stub
		return super.caseNetworkUtilizationTrigger(object);
	}

	@Override
	public Set<AbstractTriggerEvent> caseResponseTimeTrigger(final ResponseTimeTrigger object) {
		// TODO Auto-generated method stub
		return super.caseResponseTimeTrigger(object);
	}

	@Override
	public Set<AbstractTriggerEvent> caseResourceUtilizationBasedTrigger(final ResourceUtilizationBasedTrigger object) {
		// TODO Auto-generated method stub
		return super.caseResourceUtilizationBasedTrigger(object);
	}

	@Override
	public Set<AbstractTriggerEvent> caseProcessingResourceUtilizationBasedTrigger(
			final ProcessingResourceUtilizationBasedTrigger object) {
		// TODO Auto-generated method stub
		return super.caseProcessingResourceUtilizationBasedTrigger(object);
	}

}
