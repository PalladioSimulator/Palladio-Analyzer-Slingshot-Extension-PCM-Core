package org.palladiosimulator.analyzer.slingshot.provider.model.usagemodel;

import java.nio.file.Path;

import javax.inject.Named;
import javax.inject.Singleton;

import org.palladiosimulator.analyzer.slingshot.common.serialization.load.PCMFileLoader;
import org.palladiosimulator.analyzer.slingshot.module.models.AbstractModelProvider;
import org.palladiosimulator.pcm.usagemodel.UsageModel;

import com.google.inject.Provides;

// @Extension(ModelProviderExtension.class)
public class UsageModelModule extends AbstractModelProvider {

	@Override
	protected void configure() {
//		bind(UsageModel.class).toProvider(UsageModelProvider.class);
	}

	@Provides
	@Singleton
	public UsageModel provideUsageModel(@Named(RequiredUsageModelTextForm.PATH_BINDING_ID) final Path path) {
		return PCMFileLoader.load(path);
	}

}
