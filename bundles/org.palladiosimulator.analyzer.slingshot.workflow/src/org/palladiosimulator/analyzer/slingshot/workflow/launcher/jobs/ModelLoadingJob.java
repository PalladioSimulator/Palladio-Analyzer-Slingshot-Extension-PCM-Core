package org.palladiosimulator.analyzer.slingshot.workflow.launcher.jobs;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.palladiosimulator.analyzer.slingshot.module.models.ModelModule;
import org.palladiosimulator.analyzer.slingshot.workflow.configuration.SimulationWorkflowConfiguration;
import org.palladiosimulator.analyzer.slingshot.workflow.launcher.model.AllocationProvider;
import org.palladiosimulator.analyzer.slingshot.workflow.launcher.model.UsageModelProvider;
import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.usagemodel.UsageModel;

import com.google.inject.AbstractModule;

import de.uka.ipd.sdq.workflow.blackboard.Blackboard;
import de.uka.ipd.sdq.workflow.jobs.CleanupFailedException;
import de.uka.ipd.sdq.workflow.jobs.IBlackboardInteractingJob;
import de.uka.ipd.sdq.workflow.jobs.JobFailedException;
import de.uka.ipd.sdq.workflow.jobs.UserCanceledException;

/**
 * This job is responsible for loading all modules that load Model files. These
 * can then be used for dependency injection.
 * 
 * @author Julijan Katic
 */
public class ModelLoadingJob implements IBlackboardInteractingJob<Blackboard<Object>> {

	private final Logger LOGGER = Logger.getLogger(ModelLoadingJob.class);

	private Blackboard<Object> blackboard;

	private final SimulationWorkflowConfiguration configuration;

	public ModelLoadingJob(final SimulationWorkflowConfiguration configuration) {
		this.configuration = configuration;
	}

	@Override
	public void execute(final IProgressMonitor monitor) throws JobFailedException, UserCanceledException {
		LOGGER.info("*************** Loading Modules / Model Containers *****************");

		final ModelModule moduleContainer = new ModelModule();

		moduleContainer.getModelContainer().addModule(new AbstractModule() {
			@Override
			public void configure() {
				bind(SimulationWorkflowConfiguration.class).toInstance(configuration);
				bind(UsageModel.class).toProvider(UsageModelProvider.class);
				bind(Allocation.class).toProvider(AllocationProvider.class);
			}
		});

		blackboard.addPartition("MODULE", moduleContainer);

		LOGGER.info("*************** Done loading Modules ***************");
	}

	@Override
	public void cleanup(final IProgressMonitor monitor) throws CleanupFailedException {

	}

	@Override
	public String getName() {
		return ModelLoadingJob.class.getCanonicalName();
	}

	@Override
	public void setBlackboard(final Blackboard<Object> blackboard) {
		this.blackboard = blackboard;
	}

}
