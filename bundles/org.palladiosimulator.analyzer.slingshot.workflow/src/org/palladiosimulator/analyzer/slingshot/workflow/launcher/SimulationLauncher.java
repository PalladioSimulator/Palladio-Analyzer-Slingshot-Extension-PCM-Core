package org.palladiosimulator.analyzer.slingshot.workflow.launcher;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.palladiosimulator.analyzer.slingshot.workflow.api.SimulationWorkflowConfigurationConstants;
import org.palladiosimulator.analyzer.slingshot.workflow.configuration.ArchitecturalModelsConfiguration;
import org.palladiosimulator.analyzer.slingshot.workflow.configuration.SimulationWorkflowConfiguration;
import org.palladiosimulator.analyzer.slingshot.workflow.launcher.jobs.SimulationRootJob;
import org.palladiosimulator.analyzer.workflow.configurations.AbstractPCMLaunchConfigurationDelegate;

import de.uka.ipd.sdq.workflow.jobs.IJob;
import de.uka.ipd.sdq.workflow.logging.console.LoggerAppenderStruct;

public class SimulationLauncher extends AbstractPCMLaunchConfigurationDelegate<SimulationWorkflowConfiguration> {
	
	private static final Logger LOGGER = Logger.getLogger(SimulationLauncher.class.getName());

	@Override
	protected SimulationWorkflowConfiguration deriveConfiguration(ILaunchConfiguration configuration, String mode)
			throws CoreException {
		
		LOGGER.info("SimulationLauncher.deriveConfiguration");
		
		// TODO Auto-generated method stub
		return buildWorkflowConfiguration(configuration, mode);
	}

	
	@Override
	protected IJob createWorkflowJob(SimulationWorkflowConfiguration config, ILaunch launch) throws CoreException {
		return new SimulationRootJob(config, launch);
	}
	
	
	private SimulationWorkflowConfiguration buildWorkflowConfiguration(ILaunchConfiguration configuration, String mode) {
		
		SimulationWorkflowConfiguration workflowConfiguration = null;
		try {
			Map<String, Object> launchConfigurationParams = configuration.getAttributes();
			
			if (LOGGER.isDebugEnabled()) {
				for (Entry<String, Object> entry : launchConfigurationParams.entrySet()) {
					LOGGER.debug(String.format("launch configuration param ['%s':'%s']", entry.getKey(), entry.getValue()));
				}
			}
			
			ArchitecturalModelsConfiguration architecturalModels = new ArchitecturalModelsConfiguration(
					(String) launchConfigurationParams.get(SimulationWorkflowConfigurationConstants.USAGE_FILE)
					, Arrays.asList((String) launchConfigurationParams.get(SimulationWorkflowConfigurationConstants.ALLOCATION_FILE))
					, (String) launchConfigurationParams.get(SimulationWorkflowConfigurationConstants.MONITOR_REPOSITORY_FILE));
			
			workflowConfiguration = new SimulationWorkflowConfiguration(architecturalModels);
			
		} catch (CoreException e) {
			LOGGER.error("Failed to read workflow configuration from passed launch configuration. Please check the provided launch configuration", e);
		}
		
		return workflowConfiguration;
	}
	
    @Override
    protected ArrayList<LoggerAppenderStruct> setupLogging(Level logLevel) throws CoreException {
    	// FIXME: during development set debug level hard-coded to DEBUG
        ArrayList<LoggerAppenderStruct> loggerList = super.setupLogging(Level.DEBUG);
        loggerList.add(setupLogger("org.palladiosimulator.analyzer.slingshot", logLevel, Level.DEBUG == logLevel ? DETAILED_LOG_PATTERN : SHORT_LOG_PATTERN));
        return loggerList;
    }




}