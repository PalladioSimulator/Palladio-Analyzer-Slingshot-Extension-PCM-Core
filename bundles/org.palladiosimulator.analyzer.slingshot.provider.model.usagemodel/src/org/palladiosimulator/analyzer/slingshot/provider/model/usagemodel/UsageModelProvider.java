package org.palladiosimulator.analyzer.slingshot.provider.model.usagemodel;

import java.nio.file.Path;

import javax.inject.Inject;
import javax.inject.Provider;

import org.palladiosimulator.analyzer.slingshot.common.serialization.load.PCMFileLoader;
import org.palladiosimulator.pcm.usagemodel.UsageModel;

//@ModelProvider
public class UsageModelProvider implements Provider<UsageModel> {

	private final Path usageModelPath;

	@Inject
	public UsageModelProvider(final Path usageModelPath) {
		this.usageModelPath = usageModelPath;
	}

	@Override
	public UsageModel get() {
		return PCMFileLoader.load(usageModelPath);
	}

}
