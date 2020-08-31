package org.palladiosimulator.analyzer.slingshot.workflow.launcher;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.palladiosimulator.analyzer.slingshot.common.constants.model.ModelFileTypeConstants;
import org.palladiosimulator.analyzer.slingshot.workflow.configuration.ArchitecturalModelsConfiguration;
import org.palladiosimulator.analyzer.slingshot.workflow.configuration.SimulationWorkflowConfiguration;
import org.palladiosimulator.analyzer.slingshot.workflow.launcher.jobs.SimulationRootJob;
import org.palladiosimulator.analyzer.workflow.configurations.AbstractPCMLaunchConfigurationDelegate;

import de.uka.ipd.sdq.workflow.jobs.IJob;
import de.uka.ipd.sdq.workflow.logging.console.LoggerAppenderStruct;

public class SimulationLauncher extends AbstractPCMLaunchConfigurationDelegate<SimulationWorkflowConfiguration> {

	private static final Logger LOGGER = Logger.getLogger(SimulationLauncher.class.getName());

	@Override
	protected SimulationWorkflowConfiguration deriveConfiguration(final ILaunchConfiguration configuration, final String mode)
			throws CoreException {

		LOGGER.info("SimulationLauncher.deriveConfiguration");

		return buildWorkflowConfiguration(configuration, mode);
	}

	@Override
	protected IJob createWorkflowJob(final SimulationWorkflowConfiguration config, final ILaunch launch) throws CoreException {
		return new SimulationRootJob(config, launch);
	}

	private SimulationWorkflowConfiguration buildWorkflowConfiguration(final ILaunchConfiguration configuration,
			final String mode) {

		SimulationWorkflowConfiguration workflowConfiguration = null;
		try {
			final Map<String, Object> launchConfigurationParams = configuration.getAttributes();

			if (LOGGER.isDebugEnabled()) {
				for (final Entry<String, Object> entry : launchConfigurationParams.entrySet()) {
					LOGGER.debug(
							String.format("launch configuration param ['%s':'%s']", entry.getKey(), entry.getValue()));
				}
			}

			final ArchitecturalModelsConfiguration architecturalModels = new ArchitecturalModelsConfiguration(
					(String) launchConfigurationParams.get(ModelFileTypeConstants.USAGE_FILE),
					(String) launchConfigurationParams.get(ModelFileTypeConstants.ALLOCATION_FILE),
					(String) launchConfigurationParams.get(ModelFileTypeConstants.MONITOR_REPOSITORY_FILE));

			workflowConfiguration = new SimulationWorkflowConfiguration(architecturalModels);

		} catch (final CoreException e) {
			LOGGER.error(
					"Failed to read workflow configuration from passed launch configuration. Please check the provided launch configuration",
					e);
		}

		return workflowConfiguration;
	}

	@Override
	protected ArrayList<LoggerAppenderStruct> setupLogging(final Level logLevel) throws CoreException {
		// FIXME: during development set debug level hard-coded to DEBUG
		final ArrayList<LoggerAppenderStruct> loggerList = super.setupLogging(Level.DEBUG);
		loggerList.add(setupLogger("org.palladiosimulator.analyzer.slingshot", logLevel,
				Level.DEBUG == logLevel ? DETAILED_LOG_PATTERN : SHORT_LOG_PATTERN));
		return loggerList;
	}

}