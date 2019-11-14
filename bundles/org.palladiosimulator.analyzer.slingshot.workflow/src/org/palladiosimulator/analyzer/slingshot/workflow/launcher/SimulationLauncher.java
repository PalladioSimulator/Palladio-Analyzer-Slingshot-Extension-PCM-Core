package org.palladiosimulator.analyzer.slingshot.workflow.launcher;


import java.util.ArrayList;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.palladiosimulator.analyzer.workflow.configurations.AbstractPCMLaunchConfigurationDelegate;
import org.palladiosimulator.analyzer.workflow.configurations.AbstractPCMWorkflowRunConfiguration;

import de.uka.ipd.sdq.workflow.jobs.IJob;
import de.uka.ipd.sdq.workflow.logging.console.LoggerAppenderStruct;

public class SimulationLauncher extends AbstractPCMLaunchConfigurationDelegate<AbstractPCMWorkflowRunConfiguration> {
	
	private static final Logger LOGGER = Logger.getLogger(SimulationLauncher.class.getName());

	@Override
	protected AbstractPCMWorkflowRunConfiguration deriveConfiguration(ILaunchConfiguration configuration, String mode)
			throws CoreException {
		
		LOGGER.info("SimulationLauncher.deriveConfiguration");
		
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	protected IJob createWorkflowJob(AbstractPCMWorkflowRunConfiguration config, ILaunch launch) throws CoreException {
		LOGGER.info("SimulationLauncher.createWorkflowJob");
		// TODO Auto-generated method stub
		return null;
	}
	
    @Override
    protected ArrayList<LoggerAppenderStruct> setupLogging(Level logLevel) throws CoreException {
    	// FIXME: during development set debug level hard-coded to DEBUG
        ArrayList<LoggerAppenderStruct> loggerList = super.setupLogging(Level.DEBUG);
        loggerList.add(setupLogger("org.palladiosimulator.analyzer.slingshot", logLevel, Level.DEBUG == logLevel ? DETAILED_LOG_PATTERN : SHORT_LOG_PATTERN));
        return loggerList;
    }


}