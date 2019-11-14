package org.palladiosimulator.analyzer.slingshot.helper;

import java.nio.file.Path;

import org.palladiosimulator.analyzer.slingshot.common.serialization.load.UsageModelFileLoader;
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
		UsageModelFileLoader loader = new UsageModelFileLoader();
		UsageModel usageModel = loader.load(filePath);
		return usageModel;
	}

	public static UsageModel createClosedWorkloadUsagModelWithDelay(final int population, final double thinkTimeSpecification) {
		UsageModel usageModel = usageModelFactory.createUsageModel();
		UsageScenario usageScenario = usageModelFactory.createUsageScenario();

		// workload
		ClosedWorkload closedWorkload = usageModelFactory.createClosedWorkload();
		// set bi-directional reference to usage scenario
		closedWorkload.setUsageScenario_Workload(usageScenario);
		closedWorkload.setPopulation(population);
		PCMRandomVariable thinkTime = coreFactory.createPCMRandomVariable();
		thinkTime.setSpecification(String.valueOf(thinkTimeSpecification));
		thinkTime.setClosedWorkload_PCMRandomVariable(closedWorkload);
		closedWorkload.setThinkTime_ClosedWorkload(thinkTime);
		usageScenario.setWorkload_UsageScenario(closedWorkload);

		// usage behavior
		// entities
		ScenarioBehaviour behavior = usageModelFactory.createScenarioBehaviour();
		behavior.setEntityName("scenarioBehavior");
		Start startEntity = usageModelFactory.createStart();
		startEntity.setEntityName("start");
		Delay delayEntity = usageModelFactory.createDelay();
		delayEntity.setEntityName("delay");
		Stop stopEntity = usageModelFactory.createStop();
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
