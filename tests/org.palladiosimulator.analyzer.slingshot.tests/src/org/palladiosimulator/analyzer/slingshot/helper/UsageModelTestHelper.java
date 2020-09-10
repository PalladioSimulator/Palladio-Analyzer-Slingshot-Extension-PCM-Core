package org.palladiosimulator.analyzer.slingshot.helper;

import java.nio.file.Path;

import org.palladiosimulator.analyzer.slingshot.common.serialization.load.PCMFileLoader;
import org.palladiosimulator.pcm.core.PCMRandomVariable;
import org.palladiosimulator.pcm.usagemodel.ClosedWorkload;
import org.palladiosimulator.pcm.usagemodel.Delay;
import org.palladiosimulator.pcm.usagemodel.ScenarioBehaviour;
import org.palladiosimulator.pcm.usagemodel.Start;
import org.palladiosimulator.pcm.usagemodel.Stop;
import org.palladiosimulator.pcm.usagemodel.UsageModel;
import org.palladiosimulator.pcm.usagemodel.UsageScenario;

public class UsageModelTestHelper extends AbstractModelTestHelper {
	
	
	public static UsageModel createUsageModelFromFile(final Path filePath) {
		final PCMFileLoader loader = new PCMFileLoader();
		final UsageModel usageModel = loader.load(filePath);
		return usageModel;
	}

	public static UsageModel createClosedWorkloadUsagModelWithDelay(final int population, final double thinkTimeSpecification) {
		final UsageModel usageModel = usageModelFactory.createUsageModel();
		final UsageScenario usageScenario = usageModelFactory.createUsageScenario();

		// workload
		final ClosedWorkload closedWorkload = usageModelFactory.createClosedWorkload();
		// set bi-directional reference to usage scenario
		closedWorkload.setUsageScenario_Workload(usageScenario);
		closedWorkload.setPopulation(population);
		final PCMRandomVariable thinkTime = coreFactory.createPCMRandomVariable();
		thinkTime.setSpecification(String.valueOf(thinkTimeSpecification));
		thinkTime.setClosedWorkload_PCMRandomVariable(closedWorkload);
		closedWorkload.setThinkTime_ClosedWorkload(thinkTime);
		usageScenario.setWorkload_UsageScenario(closedWorkload);

		// usage behavior
		// entities
		final ScenarioBehaviour behavior = usageModelFactory.createScenarioBehaviour();
		behavior.setEntityName("scenarioBehavior");
		final Start startEntity = usageModelFactory.createStart();
		startEntity.setEntityName("start");
		final Delay delayEntity = usageModelFactory.createDelay();
		delayEntity.setEntityName("delay");
		final Stop stopEntity = usageModelFactory.createStop();
		stopEntity.setEntityName("stop");

		// references
		startEntity.setScenarioBehaviour_AbstractUserAction(behavior);
		startEntity.setSuccessor(delayEntity);
		delayEntity.setSuccessor(stopEntity);
		stopEntity.setPredecessor(delayEntity);

		behavior.setUsageScenario_SenarioBehaviour(usageScenario);
		usageScenario.setScenarioBehaviour_UsageScenario(behavior);
		usageModel.getUsageScenario_UsageModel().add(usageScenario);

		return usageModel;
	}
}
