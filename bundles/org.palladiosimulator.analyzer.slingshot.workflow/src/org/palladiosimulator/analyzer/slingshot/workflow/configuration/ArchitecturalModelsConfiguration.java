package org.palladiosimulator.analyzer.slingshot.workflow.configuration;

import java.util.ArrayList;
import java.util.List;

public class ArchitecturalModelsConfiguration {
	
	private String allocationFile;
    private String usageModelFile;
	// Simulizar intergration
	private String monitorRepositoryFile;
	
	public ArchitecturalModelsConfiguration(final String usageModelFile, final String allocationFile, final String monitorRepositoryFile) {
		
		this.usageModelFile = usageModelFile;
		this.monitorRepositoryFile = monitorRepositoryFile;
		this.allocationFile = allocationFile;
	}

	public String getMonitorRepositoryFile() {
		return monitorRepositoryFile;
	}

	
	public String getAllocationFile() {
		return allocationFile;
	}
//	
//	public List<String> getAllocationFiles() {
//		 List<String> copyAllocationFiles = new ArrayList<String>();
//		 copyAllocationFiles.addAll(allocationFiles);
//		 return copyAllocationFiles;
//	}

	public String getUsageModelFile() {
		return usageModelFile;
	}

}
