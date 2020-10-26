package org.palladiosimulator.analyzer.slingshot.workflow.launcher.model;

import java.nio.file.Paths;

import javax.inject.Inject;
import javax.inject.Provider;

import org.palladiosimulator.analyzer.slingshot.common.serialization.load.PCMFileLoader;
import org.palladiosimulator.analyzer.slingshot.workflow.configuration.SimulationWorkflowConfiguration;
import org.palladiosimulator.pcm.allocation.Allocation;

public class AllocationProvider implements Provider<Allocation> {

	private final SimulationWorkflowConfiguration configuration;

	@Inject
	public AllocationProvider(final SimulationWorkflowConfiguration configuration) {
		this.configuration = configuration;
	}

	@Override
	public Allocation get() {
		return PCMFileLoader.load(Paths.get(configuration.getAllocationFiles().get(0)));
	}

}
