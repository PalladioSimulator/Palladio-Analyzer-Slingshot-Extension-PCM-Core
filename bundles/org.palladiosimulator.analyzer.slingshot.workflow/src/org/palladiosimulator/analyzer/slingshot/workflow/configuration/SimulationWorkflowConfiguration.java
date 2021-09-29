package org.palladiosimulator.analyzer.slingshot.workflow.configuration;

import java.util.List;

import org.palladiosimulator.analyzer.workflow.configurations.AbstractPCMWorkflowRunConfiguration;

import de.uka.ipd.sdq.simucomframework.SimuComConfig;

public class SimulationWorkflowConfiguration extends AbstractPCMWorkflowRunConfiguration {

	private final ArchitecturalModelsConfiguration inputModels;
	private final SimuComConfig simuConConfig;

	private final String monitorRepositoryFile;

	public SimulationWorkflowConfiguration(final ArchitecturalModelsConfiguration architecturalModels,
			final SimuComConfig config) {
		this.inputModels = architecturalModels;
		this.simuConConfig = config; // TODO

		/*
		 * workaround: allocation files are required by the parent class
		 * AbstractPCMWorkflowRunConfiguration.validateAndFreeze when loading PCMModels;
		 * this existence check for PCM models should be done during configuration
		 * validation !!! needs refactoring for simulation it is current not required;
		 * therefore pass empty list in order to successfully execute workflow
		 */
		this.setUsageModelFile(this.inputModels.getUsageModelFile());
		this.setAllocationFiles(List.of(this.inputModels.getAllocationFile()));
		this.monitorRepositoryFile = this.inputModels.getMonitorRepositoryFile();
	}

	@Override
	public String getErrorMessage() {
		// configuration validation is already done in the LaunchConfiguration class
		// currently no error messages available; return null otherwise workflow
		// validation fails
		return null;
	}

	@Override
	public void setDefaults() {
		// do nothing
	}

	@Override
	public List<String> getPCMModelFiles() {
		// Only returns usage and allocation model files.
		final List<String> modelFiles = super.getPCMModelFiles();
		modelFiles.add(this.monitorRepositoryFile);
		return modelFiles;
	}

	public SimuComConfig getConfiguration() {
		return this.simuConConfig;
	}
}
