package org.palladiosimulator.analyzer.slingshot.workflow.launcher.jobs;

import java.nio.file.Paths;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.palladiosimulator.analyzer.slingshot.common.constants.model.ModelFileTypeConstants;
import org.palladiosimulator.analyzer.slingshot.common.serialization.load.PCMFileLoader;
import org.palladiosimulator.analyzer.slingshot.workflow.configuration.SimulationWorkflowConfiguration;

import de.uka.ipd.sdq.workflow.jobs.CleanupFailedException;
import de.uka.ipd.sdq.workflow.jobs.IBlackboardInteractingJob;
import de.uka.ipd.sdq.workflow.jobs.JobFailedException;
import de.uka.ipd.sdq.workflow.jobs.UserCanceledException;
import de.uka.ipd.sdq.workflow.mdsd.blackboard.MDSDBlackboard;

/**
 * This job is responsible for loading all modules that load Model files. These
 * can then be used for dependency injection.
 * 
 * @author Julijan Katic
 */
public class ModelLoadingJob implements IBlackboardInteractingJob<MDSDBlackboard> {

	private final Logger LOGGER = Logger.getLogger(ModelLoadingJob.class);

	private MDSDBlackboard blackboard;

	private final SimulationWorkflowConfiguration configuration;

	public ModelLoadingJob(final SimulationWorkflowConfiguration configuration) {
		this.configuration = configuration;
	}

	@Override
	public void execute(final IProgressMonitor monitor) throws JobFailedException, UserCanceledException {
		this.LOGGER.info("*************** Loading Modules / Model Containers *****************");

//		final ModelModule moduleContainer = new ModelModule();
//
//		moduleContainer.getModelContainer().addModule(new AbstractModule() {
//			@Override
//			public void configure() {
//				bind(SimulationWorkflowConfiguration.class).toInstance(configuration);
//				bind(UsageModel.class).toProvider(UsageModelProvider.class);
//				bind(Allocation.class).toProvider(AllocationProvider.class);
//			}
//		});

		// blackboard.addPartition("MODULE", moduleContainer);

		this.blackboard.addPartition(ModelFileTypeConstants.USAGE_FILE,
				PCMFileLoader.load(Paths.get(this.configuration.getUsageModelFile())));
		this.blackboard.addPartition(ModelFileTypeConstants.ALLOCATION_FILE,
				PCMFileLoader.load(Paths.get(this.configuration.getAllocationFiles().get(0))));

		this.LOGGER.info("*************** Done loading Modules ***************");
	}

	@Override
	public void cleanup(final IProgressMonitor monitor) throws CleanupFailedException {

	}

	@Override
	public String getName() {
		return ModelLoadingJob.class.getCanonicalName();
	}

	@Override
	public void setBlackboard(final MDSDBlackboard blackboard) {
		this.blackboard = blackboard;
	}

}
