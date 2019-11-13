package org.palladiosimulator.analyzer.slingshot.helper;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.palladiosimulator.analyzer.slingshot.simulation.api.SimulationModel;
import org.palladiosimulator.analyzer.slingshot.simulation.core.SimulizarSimulationModel;
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
		
		EMFResourceSetInitializerTestHelper.initEMF();
		
		ResourceSet resourceSet = new ResourceSetImpl();
		URI fileURI = URI.createFileURI(filePath.toString());
		Resource usageModelResource = resourceSet.getResource(fileURI, true);
		return (UsageModel)usageModelResource.getContents().get(0);
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
