package org.palladiosimulator.analyzer.slingshot.workflow.launcher.jobs;

import org.eclipse.debug.core.ILaunch;
import org.palladiosimulator.analyzer.slingshot.workflow.configuration.SimulationWorkflowConfiguration;
import org.palladiosimulator.analyzer.workflow.jobs.LoadModelIntoBlackboardJob;
import org.palladiosimulator.analyzer.workflow.jobs.PreparePCMBlackboardPartitionJob;

import de.uka.ipd.sdq.workflow.jobs.ICompositeJob;
import de.uka.ipd.sdq.workflow.jobs.SequentialBlackboardInteractingJob;
import de.uka.ipd.sdq.workflow.mdsd.blackboard.MDSDBlackboard;

public class SimulationRootJob extends SequentialBlackboardInteractingJob<MDSDBlackboard> implements ICompositeJob {

	public SimulationRootJob(final SimulationWorkflowConfiguration config, final ILaunch launch) {
		super(SimulationRootJob.class.getName(), false);

		// add all contained jobs here
		// LoadModelIntoBlackboardJob.parseUriAndAddModelLoadJob(getName(), null);
		this.addJob(new PreparePCMBlackboardPartitionJob());
		config.getPCMModelFiles()
				.forEach(modelFile -> LoadModelIntoBlackboardJob.parseUriAndAddModelLoadJob(modelFile, this));

		// this.addJob(new ModelLoadingJob(config));
		// this.addJob(SimulationWorkflowJobFactory.createSimulationJob(config));
		// this.addJob(new LoadPCMModelsIntoBlackboardJob(config));
		this.addJob(new SimulationJob(config));
	}

}
