package org.palladiosimulator.analyzer.slingshot.workflow.launcher.model;

import java.nio.file.Paths;

import javax.inject.Inject;
import javax.inject.Provider;

import org.palladiosimulator.analyzer.slingshot.common.serialization.load.PCMFileLoader;
import org.palladiosimulator.analyzer.slingshot.workflow.configuration.SimulationWorkflowConfiguration;
import org.palladiosimulator.pcm.usagemodel.UsageModel;

public class UsageModelProvider implements Provider<UsageModel> {

	private final SimulationWorkflowConfiguration configuration;

	@Inject
	public UsageModelProvider(final SimulationWorkflowConfiguration configuration) {
		this.configuration = configuration;
	}

	@Override
	public UsageModel get() {
		return PCMFileLoader.load(Paths.get(configuration.getUsageModelFile()));
	}

}
