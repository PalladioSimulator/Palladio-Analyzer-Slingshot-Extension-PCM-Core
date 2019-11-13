package org.palladiosimulator.analyzer.slingshot.repositories.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.palladiosimulator.analyzer.slingshot.repositories.UsageModelRepository;
import org.palladiosimulator.pcm.usagemodel.AbstractUserAction;
import org.palladiosimulator.pcm.usagemodel.ClosedWorkload;
import org.palladiosimulator.pcm.usagemodel.UsageModel;
import org.palladiosimulator.pcm.usagemodel.UsageScenario;
import org.palladiosimulator.pcm.usagemodel.Workload;

public class UsageModelRepositoryImpl implements UsageModelRepository {
	
	private final Logger LOGGER = Logger.getLogger(UsageModelRepositoryImpl.class);
	
	private UsageModel usageModel;

	public UsageModelRepositoryImpl(final UsageModel usageModel) {
		this.usageModel = usageModel;
	}

	public List<UsageScenario> findAllUsageScenarios() {
		EList<UsageScenario> usageScenarios = usageModel.getUsageScenario_UsageModel();
		return usageScenarios;
	}


	@Override
	public Workload findWorkloadForUsageScenario(UsageScenario usageScenario) {
		for (UsageScenario scenario : findAllUsageScenarios()) {
			if (scenario.getId().equals(usageScenario.getId())) {
				return scenario.getWorkload_UsageScenario();
			}
		}
		return null;
	}

	@Override
	public void load(UsageModel usageModel) {
		this.usageModel = usageModel;
	}

	@Override
	public int findClosedWorkloadPopulation(ClosedWorkload workload) {
		return workload.getPopulation();
	}

	@Override
	public AbstractUserAction findFirstActionOf(UsageScenario scenario) {
		return scenario.getScenarioBehaviour_UsageScenario().getActions_ScenarioBehaviour().get(0);
	}

	
}
