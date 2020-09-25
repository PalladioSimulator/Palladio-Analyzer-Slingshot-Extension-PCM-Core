package org.palladiosimulator.analyzer.slingshot.workflow.launcher.jobs;

import java.nio.file.Path;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.palladiosimulator.analyzer.slingshot.common.serialization.load.PCMFileLoader;
import org.palladiosimulator.analyzer.slingshot.simulation.api.Simulation;
import org.palladiosimulator.analyzer.slingshot.simulation.api.SimulationFactory;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.model.SimulationModel;
import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.usagemodel.UsageModel;

import de.uka.ipd.sdq.workflow.jobs.CleanupFailedException;
import de.uka.ipd.sdq.workflow.jobs.IBlackboardInteractingJob;
import de.uka.ipd.sdq.workflow.jobs.JobFailedException;
import de.uka.ipd.sdq.workflow.jobs.UserCanceledException;
import de.uka.ipd.sdq.workflow.mdsd.blackboard.MDSDBlackboard;

public class SimulationJob implements IBlackboardInteractingJob<MDSDBlackboard> {
	
	private static final Logger LOGGER = Logger.getLogger(SimulationJob.class.getName());

	private MDSDBlackboard blackboard;
	
	private final Path usageModelPath;
	private final Path allocationModelPath;
	private final PCMFileLoader pcmFileLoader;
	
	public SimulationJob(final PCMFileLoader pcmFileLoader,  final Path usageModelPath, final Path allocationModelPath) {
		this.usageModelPath = usageModelPath;
		this.pcmFileLoader = pcmFileLoader;
		this.allocationModelPath = allocationModelPath;
	}

	@Override
	public void execute(final IProgressMonitor monitor) throws JobFailedException, UserCanceledException {
		LOGGER.info("**** SimulationJob.execute ****");
		
		final UsageModel usageModel = pcmFileLoader.load(usageModelPath);
		final Allocation allocation = pcmFileLoader.load(allocationModelPath);
		final SimulationModel model = SimulationFactory.createSimulizarSimulationModel(usageModel, allocation);
		
		Simulation simulation;
						
		try {
			simulation = SimulationFactory.createSimulation();
			simulation.init(model);
			simulation.startSimulation();
		} catch (final Exception e) {
			throw new JobFailedException("Simulation Could Not Be Created", e);
		}
		
		
		LOGGER.info("**** SimulationJob.execute  - Done ****");
	}
	


	@Override
	public void cleanup(final IProgressMonitor monitor) throws CleanupFailedException {
		/**
		 * 
		 * nothing to do here
		 * 
		 * */
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

