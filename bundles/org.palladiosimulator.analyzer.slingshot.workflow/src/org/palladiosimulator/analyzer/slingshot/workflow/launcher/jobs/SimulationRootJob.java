package org.palladiosimulator.analyzer.slingshot.workflow.launcher.jobs;

import java.nio.file.Paths;

import org.eclipse.debug.core.ILaunch;
import org.palladiosimulator.analyzer.slingshot.workflow.configuration.SimulationWorkflowConfiguration;

import de.uka.ipd.sdq.workflow.jobs.ICompositeJob;
import de.uka.ipd.sdq.workflow.jobs.SequentialBlackboardInteractingJob;
import de.uka.ipd.sdq.workflow.mdsd.blackboard.MDSDBlackboard;

public class SimulationRootJob extends SequentialBlackboardInteractingJob<MDSDBlackboard> implements ICompositeJob {

	public SimulationRootJob(SimulationWorkflowConfiguration config, ILaunch launch) {
		super(SimulationRootJob.class.getName(), false);
		
		// add all contained jobs here
		 this.addJob(SimulationWorkflowJobFactory.createSimulationJob(config));

	}


}
