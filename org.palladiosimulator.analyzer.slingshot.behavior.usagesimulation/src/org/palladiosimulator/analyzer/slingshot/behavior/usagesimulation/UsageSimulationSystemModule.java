package org.palladiosimulator.analyzer.slingshot.behavior.usagesimulation;

import org.palladiosimulator.analyzer.slingshot.behavior.usagesimulation.ui.UsageModelLaunchConfig;
import org.palladiosimulator.analyzer.slingshot.behavior.usagesimulation.ui.UsageModelProvider;
import org.palladiosimulator.analyzer.slingshot.behavior.usagesimulation.ui.UsageModelWorkflowConfig;
import org.palladiosimulator.analyzer.slingshot.core.extension.AbstractSlingshotExtension;
import org.palladiosimulator.pcm.usagemodel.UsageModel;

public class UsageSimulationSystemModule extends AbstractSlingshotExtension {

	@Override
	protected void configure() {
		install(UsageModelLaunchConfig.class);
		install(UsageModelWorkflowConfig.class);
		provideModel(UsageModel.class, UsageModelProvider.class);
	}

	@Override
	public String getName() {
		return UsageSimulationSystemModule.class.getSimpleName();
	}


}
