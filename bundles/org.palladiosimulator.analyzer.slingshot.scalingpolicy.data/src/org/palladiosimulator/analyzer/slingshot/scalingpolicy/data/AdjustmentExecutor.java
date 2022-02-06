package org.palladiosimulator.analyzer.slingshot.scalingpolicy.data;

import spd.targetgroup.TargetGroup;

public interface AdjustmentExecutor {

	public void onTrigger(final TargetGroup targetGroup);

}
