package org.palladiosimulator.analyzer.slingshot.workflow.launcher.jobs;

import org.eclipse.debug.core.ILaunch;
import org.palladiosimulator.analyzer.slingshot.workflow.configuration.SimulationWorkflowConfiguration;

import de.uka.ipd.sdq.workflow.blackboard.Blackboard;
import de.uka.ipd.sdq.workflow.jobs.ICompositeJob;
import de.uka.ipd.sdq.workflow.jobs.SequentialBlackboardInteractingJob;

public class SimulationRootJob extends SequentialBlackboardInteractingJob<Blackboard<Object>> implements ICompositeJob {

	public SimulationRootJob(final SimulationWorkflowConfiguration config, final ILaunch launch) {
		super(SimulationRootJob.class.getName(), false);

		// add all contained jobs here
		this.addJob(new ModelLoadingJob(config));
		// this.addJob(new BehaviorLoadingJob());
		// this.addJob(SimulationWorkflowJobFactory.createSimulationJob(config));
		this.addJob(new SimulationJob());
	}

}
