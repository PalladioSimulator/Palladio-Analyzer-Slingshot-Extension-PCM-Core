package org.palladiosimulator.analyzer.slingshot.scalingpolicy.interpreter;

import java.util.Set;

import org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.AbstractTriggerEvent;
import org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.AdjustmentExecutor;
import org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.PointInTimeTriggered;
import org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.TriggerContext;

import spd.ScalingPolicy;
import spd.scalingtrigger.PointInTimeTrigger;
import spd.scalingtrigger.util.ScalingtriggerSwitch;
import spd.targetgroup.TargetGroup;

public class ScalingTriggerInterpreter extends ScalingtriggerSwitch<Set<AbstractTriggerEvent>> {

	private final ScalingPolicy parent;

	public ScalingTriggerInterpreter(final ScalingPolicy parent) {
		this.parent = parent;
	}

	@Override
	public Set<AbstractTriggerEvent> casePointInTimeTrigger(final PointInTimeTrigger object) {
		final AdjustmentExecutor executor = (new AdjustmentTypeInterpreter()).doSwitch(this.parent.getAdjustmenttype());
		final TriggerContext context = TriggerContext.builder()
				.withAdjustmentExecutor(executor)
				.withTargetGroup((TargetGroup) this.parent.getTargetgroup())
				.build();

		return Set.of(new PointInTimeTriggered(context, object.getPointInTime()));
	}

}
