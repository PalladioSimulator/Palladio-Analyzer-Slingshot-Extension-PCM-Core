package org.palladiosimulator.analyzer.slingshot.workflow.configuration;

import java.util.List;

import org.palladiosimulator.analyzer.workflow.configurations.AbstractPCMWorkflowRunConfiguration;

public class SimulationWorkflowConfiguration extends AbstractPCMWorkflowRunConfiguration {

	private final ArchitecturalModelsConfiguration inputModels;

	public SimulationWorkflowConfiguration(final ArchitecturalModelsConfiguration architecturalModels) {
		this.inputModels = architecturalModels;

		/**
		 * workaround: allocation files are required by the parent class
		 * AbstractPCMWorkflowRunConfiguration.validateAndFreeze when loading PCMModels;
		 * this existence check for PCM models should be done during configuration
		 * validation !!! needs refactoring for simulation it is current not required;
		 * therefore pass empty list in order to successfully execute workflow
		 */
		this.setUsageModelFile(inputModels.getUsageModelFile());
		this.setAllocationFiles(List.of(inputModels.getAllocationFile()));
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

	}

}
