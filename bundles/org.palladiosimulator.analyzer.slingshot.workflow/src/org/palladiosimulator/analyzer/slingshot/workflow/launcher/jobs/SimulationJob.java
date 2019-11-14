package org.palladiosimulator.analyzer.slingshot.workflow.launcher.jobs;

import java.nio.file.Path;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.palladiosimulator.analyzer.slingshot.common.serialization.load.UsageModelFileLoader;
import org.palladiosimulator.analyzer.slingshot.simulation.api.Simulation;
import org.palladiosimulator.analyzer.slingshot.simulation.api.SimulationFactory;
import org.palladiosimulator.pcm.usagemodel.UsageModel;

import de.uka.ipd.sdq.workflow.jobs.CleanupFailedException;
import de.uka.ipd.sdq.workflow.jobs.IBlackboardInteractingJob;
import de.uka.ipd.sdq.workflow.jobs.JobFailedException;
import de.uka.ipd.sdq.workflow.jobs.UserCanceledException;
import de.uka.ipd.sdq.workflow.mdsd.blackboard.MDSDBlackboard;

public class SimulationJob implements IBlackboardInteractingJob<MDSDBlackboard> {
	
	private static final Logger LOGGER = Logger.getLogger(SimulationJob.class.getName());

	private MDSDBlackboard blackboard;
	
	private UsageModelFileLoader usageModelLoader;
	private Path usageModelPath;
	
	public SimulationJob(final UsageModelFileLoader fileLoader, final Path usageModelPath) {
		this.usageModelPath = usageModelPath;
		this.usageModelLoader = fileLoader;
	}

	@Override
	public void execute(IProgressMonitor monitor) throws JobFailedException, UserCanceledException {
		LOGGER.info("**** SimulationJob.execute ****");
		
		UsageModel usageModel = usageModelLoader.load(usageModelPath);
		Simulation simulation = SimulationFactory.createSimulation(usageModel);
		
		simulation.init();
		simulation.startSimulation();
		
		LOGGER.info("**** SimulationJob.execute  - Done ****");
	}
	


	@Override
	public void cleanup(IProgressMonitor monitor) throws CleanupFailedException {
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
	public void setBlackboard(MDSDBlackboard blackboard) {
		this.blackboard = blackboard;
	}

}
