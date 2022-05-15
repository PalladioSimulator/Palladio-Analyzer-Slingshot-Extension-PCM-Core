package org.palladiosimulator.analyzer.slingshot.workflow.launcher;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.palladiosimulator.analyzer.slingshot.common.constants.model.ModelFileTypeConstants;
import org.palladiosimulator.analyzer.slingshot.workflow.configuration.ArchitecturalModelsConfiguration;
import org.palladiosimulator.analyzer.slingshot.workflow.configuration.SimulationWorkflowConfiguration;
import org.palladiosimulator.analyzer.slingshot.workflow.configuration.SlingshotSpecificWorkflowConfiguration;
import org.palladiosimulator.analyzer.slingshot.workflow.launcher.jobs.SimulationRootJob;
import org.palladiosimulator.analyzer.workflow.configurations.AbstractPCMLaunchConfigurationDelegate;

import de.uka.ipd.sdq.simucomframework.SimuComConfig;
import de.uka.ipd.sdq.workflow.jobs.IJob;
import de.uka.ipd.sdq.workflow.logging.console.LoggerAppenderStruct;

public class SimulationLauncher extends AbstractPCMLaunchConfigurationDelegate<SimulationWorkflowConfiguration> {

	private final Logger LOGGER = Logger.getLogger(SimulationLauncher.class);

	@Override
	protected SimulationWorkflowConfiguration deriveConfiguration(final ILaunchConfiguration configuration,
			final String mode)
			throws CoreException {

		LOGGER.info("SimulationLauncher.deriveConfiguration");

		return this.buildWorkflowConfiguration(configuration, mode);
	}

	@Override
	protected IJob createWorkflowJob(final SimulationWorkflowConfiguration config, final ILaunch launch)
			throws CoreException {
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
					(String) launchConfigurationParams.get(ModelFileTypeConstants.MONITOR_REPOSITORY_FILE),
					(String) launchConfigurationParams.get(ModelFileTypeConstants.SCALING_POLICY_DEFINITION_FILE));
			
			final SlingshotSpecificWorkflowConfiguration slingshotConfig = SlingshotSpecificWorkflowConfiguration.builder()
					.withLogFile((String) launchConfigurationParams.get(ModelFileTypeConstants.LOG_FILE))
					.build();

			//this.setupLogFileAppender(slingshotConfig.getLogFileName());
			
			// TODO: As of now, we just let it debug.
			final SimuComConfig config = new SimuComConfig(launchConfigurationParams, true);

			workflowConfiguration = new SimulationWorkflowConfiguration(architecturalModels, config, slingshotConfig);
			
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
		loggerList.add(this.setupLogger("org.palladiosimulator.analyzer.slingshot", logLevel,
				Level.DEBUG == logLevel ? DETAILED_LOG_PATTERN : SHORT_LOG_PATTERN));
		return loggerList;
	}

	private void setupLogFileAppender(final String logFileName) {
		final FileAppender fa = new FileAppender();
		fa.setName("FileLogger");
		
		final Path path = new Path(logFileName);
		LOGGER.info(path.toFile().getAbsolutePath());
		fa.setFile(path.toFile().getAbsolutePath());
		fa.setLayout(new PatternLayout(DETAILED_LOG_PATTERN));
		fa.setThreshold(Level.DEBUG);
		fa.setAppend(true);
		fa.activateOptions();
		
		Logger.getRootLogger().addAppender(fa);
		Logger.getRootLogger().getLoggerRepository().resetConfiguration();
	}
}