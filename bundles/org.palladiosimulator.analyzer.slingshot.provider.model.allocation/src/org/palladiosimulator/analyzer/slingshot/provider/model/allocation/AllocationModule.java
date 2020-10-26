package org.palladiosimulator.analyzer.slingshot.provider.model.allocation;

import java.nio.file.Path;

import javax.inject.Named;
import javax.inject.Singleton;

import org.palladiosimulator.analyzer.slingshot.common.serialization.load.PCMFileLoader;
import org.palladiosimulator.analyzer.slingshot.module.models.AbstractModelProvider;
import org.palladiosimulator.pcm.allocation.Allocation;

import com.google.inject.Provides;

public class AllocationModule extends AbstractModelProvider {

	@Override
	protected void configure() {

	}

	@Provides
	@Singleton
	public Allocation allocationModel(@Named(RequiredAllocationTextForm.PATH_BINDER_ID) final Path path) {
		return PCMFileLoader.load(path);
	}

}
