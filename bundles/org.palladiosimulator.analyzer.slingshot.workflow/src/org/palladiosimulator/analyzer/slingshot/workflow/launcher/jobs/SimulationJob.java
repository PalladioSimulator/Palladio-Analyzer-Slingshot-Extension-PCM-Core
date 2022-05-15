package org.palladiosimulator.analyzer.slingshot.workflow.launcher.jobs;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.palladiosimulator.analyzer.slingshot.simulation.api.PCMPartitionManager;
import org.palladiosimulator.analyzer.slingshot.simulation.core.SlingshotComponent;
import org.palladiosimulator.analyzer.slingshot.simulation.core.SlingshotModel;
import org.palladiosimulator.analyzer.slingshot.workflow.configuration.SimulationWorkflowConfiguration;
import org.palladiosimulator.analyzer.workflow.ConstantsContainer;
import org.palladiosimulator.analyzer.workflow.blackboard.PCMResourceSetPartition;
import org.palladiosimulator.monitorrepository.MonitorRepository;
import org.palladiosimulator.monitorrepository.MonitorRepositoryPackage;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import de.uka.ipd.sdq.simucomframework.SimuComConfig;
import de.uka.ipd.sdq.workflow.jobs.CleanupFailedException;
import de.uka.ipd.sdq.workflow.jobs.IBlackboardInteractingJob;
import de.uka.ipd.sdq.workflow.jobs.JobFailedException;
import de.uka.ipd.sdq.workflow.jobs.UserCanceledException;
import de.uka.ipd.sdq.workflow.mdsd.blackboard.MDSDBlackboard;
import spd.SPD;
import spd.SpdPackage;

/**
 * This class is responsible for starting and monitoring the simulation. It
 * executes the simulation and also loads the necessary model files that are
 * needed for this job.
 * 
 * @author Julijan Katic
 */
public class SimulationJob implements IBlackboardInteractingJob<MDSDBlackboard> {

	private static final Logger LOGGER = Logger.getLogger(SimulationJob.class.getName());

	private MDSDBlackboard blackboard;

	private final SimulationWorkflowConfiguration configuration;

	public SimulationJob(final SimulationWorkflowConfiguration config) {
		this.configuration = config;
	}

	@Override
	public void execute(final IProgressMonitor monitor) throws JobFailedException, UserCanceledException {
		LOGGER.info("**** SimulationJob.execute ****");

		final SlingshotModel model = this.loadModelFromBlackboard();

		final SlingshotComponent component = SlingshotComponent.builder()
				.withModule(model)
				.withModule(new AbstractModule() {

					@Provides
					public SimuComConfig config() {
						return SimulationJob.this.configuration.getConfiguration();
					}

					@Provides
					public PCMPartitionManager partitionManager() {
						return new PCMPartitionManager(SimulationJob.this.blackboard);
					}

					@Override
					protected void configure() {

					}

				})
				.build();

		try {
			component.getSimulation().init();
			component.getSimulation().startSimulation();
		} catch (final Exception e) {
			throw new JobFailedException("Simulation Could Not Be Created", e);
		}

		LOGGER.info("**** SimulationJob.execute  - Done ****");
	}

	private SlingshotModel loadModelFromBlackboard() {
		final PCMResourceSetPartition partition = (PCMResourceSetPartition) this.blackboard
				.getPartition(ConstantsContainer.DEFAULT_PCM_INSTANCE_PARTITION_ID);
		final SlingshotModel model = SlingshotModel.builder()
				.withAllocationModel(partition.getAllocation())
				.withUsageModel(partition.getUsageModel())
				.withMonitorinRepositoryFile((MonitorRepository) partition
						.getElement(MonitorRepositoryPackage.eINSTANCE.getMonitorRepository()).get(0))
				.withSpdFile((SPD) partition.getElement(SpdPackage.eINSTANCE.getSPD()).get(0))
				.build();
		return model;
	}

	@Override
	public void cleanup(final IProgressMonitor monitor) throws CleanupFailedException {

	}

	@Override
	public String getName() {
		return this.getClass().getCanonicalName();
	}

	@Override
	public void setBlackboard(final MDSDBlackboard blackboard) {
		this.blackboard = blackboard;
	}

}
