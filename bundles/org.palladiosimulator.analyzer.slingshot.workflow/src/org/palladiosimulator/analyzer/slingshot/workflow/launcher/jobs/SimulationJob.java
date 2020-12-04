package org.palladiosimulator.analyzer.slingshot.workflow.launcher.jobs;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.palladiosimulator.analyzer.slingshot.module.models.ModelModule;
import org.palladiosimulator.analyzer.slingshot.simulation.api.Simulation;
import org.palladiosimulator.analyzer.slingshot.simulation.api.SimulationFactory;

import de.uka.ipd.sdq.workflow.blackboard.Blackboard;
import de.uka.ipd.sdq.workflow.jobs.CleanupFailedException;
import de.uka.ipd.sdq.workflow.jobs.IBlackboardInteractingJob;
import de.uka.ipd.sdq.workflow.jobs.JobFailedException;
import de.uka.ipd.sdq.workflow.jobs.UserCanceledException;

/**
 * This class is responsible for starting and monitoring the simulation. It
 * executes the simulation and also loads the necessary model files that are
 * needed for this job.
 * 
 * @author Julijan Katic
 */
public class SimulationJob implements IBlackboardInteractingJob<Blackboard<Object>> {

	private static final Logger LOGGER = Logger.getLogger(SimulationJob.class.getName());

	private Blackboard<Object> blackboard;

	private ModelModule modelModule;

	private Simulation simulation;

	@Override
	public void execute(final IProgressMonitor monitor) throws JobFailedException, UserCanceledException {
		LOGGER.info("**** SimulationJob.execute ****");

		modelModule = (ModelModule) blackboard.getPartition("MODULE");

		try {
			simulation = SimulationFactory.createSimulation();
			simulation.init(modelModule);
			simulation.startSimulation();
		} catch (final Exception e) {
			throw new JobFailedException("Simulation Could Not Be Created", e);
		}

		LOGGER.info("**** SimulationJob.execute  - Done ****");
	}

	@Override
	public void cleanup(final IProgressMonitor monitor) throws CleanupFailedException {
		simulation.stopSimulation();
	}

	@Override
	public String getName() {
		return this.getClass().getCanonicalName();
	}

	@Override
	public void setBlackboard(final Blackboard<Object> blackboard) {
		this.blackboard = blackboard;
	}

}
