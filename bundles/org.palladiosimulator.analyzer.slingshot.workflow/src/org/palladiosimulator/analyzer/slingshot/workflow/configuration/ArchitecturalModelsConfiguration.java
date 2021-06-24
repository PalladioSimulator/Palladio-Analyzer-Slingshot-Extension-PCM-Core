package org.palladiosimulator.analyzer.slingshot.workflow.configuration;

public class ArchitecturalModelsConfiguration {

	private final String allocationFile;
	private final String usageModelFile;
	// Simulizar intergration
	private final String monitorRepositoryFile;

	public ArchitecturalModelsConfiguration(final String usageModelFile, final String allocationFile,
			final String monitorRepositoryFile) {
		this.usageModelFile = usageModelFile;
		this.monitorRepositoryFile = monitorRepositoryFile;
		this.allocationFile = allocationFile;
	}

	public String getMonitorRepositoryFile() {
		return this.monitorRepositoryFile;
	}

	public String getAllocationFile() {
		return this.allocationFile;
	}
//	
//	public List<String> getAllocationFiles() {
//		 List<String> copyAllocationFiles = new ArrayList<String>();
//		 copyAllocationFiles.addAll(allocationFiles);
//		 return copyAllocationFiles;
//	}

	public String getUsageModelFile() {
		return this.usageModelFile;
	}

}
