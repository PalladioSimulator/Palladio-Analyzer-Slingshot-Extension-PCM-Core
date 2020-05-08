package org.palladiosimulator.analyzer.slingshot.workflow.launcher.jobs;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.palladiosimulator.analyzer.slingshot.common.serialization.load.PCMFileLoader;
import org.palladiosimulator.analyzer.slingshot.workflow.configuration.SimulationWorkflowConfiguration;

import de.uka.ipd.sdq.workflow.jobs.IJob;

public class SimulationWorkflowJobFactory {

	public static IJob createSimulationJob(final SimulationWorkflowConfiguration configuration) {
		Path usageModelPath = Paths.get(configuration.getUsageModelFile());
		Path allocationModelPath = Paths.get(configuration.getAllocationFiles().get(0));
		return new SimulationJob(new PCMFileLoader(), usageModelPath, allocationModelPath);
	}

}
