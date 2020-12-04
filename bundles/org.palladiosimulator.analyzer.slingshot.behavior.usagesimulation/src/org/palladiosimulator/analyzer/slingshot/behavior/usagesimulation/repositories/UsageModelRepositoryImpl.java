package org.palladiosimulator.analyzer.slingshot.behavior.usagesimulation.repositories;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.palladiosimulator.pcm.usagemodel.AbstractUserAction;
import org.palladiosimulator.pcm.usagemodel.ClosedWorkload;
import org.palladiosimulator.pcm.usagemodel.UsageModel;
import org.palladiosimulator.pcm.usagemodel.UsageScenario;
import org.palladiosimulator.pcm.usagemodel.UsagemodelFactory;
import org.palladiosimulator.pcm.usagemodel.Workload;

public class UsageModelRepositoryImpl implements UsageModelRepository {

	private final Logger LOGGER = Logger.getLogger(UsageModelRepositoryImpl.class);

	private UsageModel usageModel;

	public UsageModelRepositoryImpl() {
		this.usageModel = UsagemodelFactory.eINSTANCE.createUsageModel();
	}

	@Override
	public List<UsageScenario> findAllUsageScenarios() {
		final EList<UsageScenario> usageScenarios = usageModel.getUsageScenario_UsageModel();
		return usageScenarios;
	}

	@Override
	public Workload findWorkloadForUsageScenario(final UsageScenario usageScenario) {
		for (final UsageScenario scenario : findAllUsageScenarios()) {
			if (scenario.getId().equals(usageScenario.getId())) {
				return scenario.getWorkload_UsageScenario();
			}
		}
		return null;
	}

	@Override
	public void load(final UsageModel usageModel) {
		this.usageModel = usageModel;
	}

	@Override
	public int findClosedWorkloadPopulation(final ClosedWorkload workload) {
		return workload.getPopulation();
	}

	@Override
	public AbstractUserAction findFirstActionOf(final UsageScenario scenario) {
		return scenario.getScenarioBehaviour_UsageScenario().getActions_ScenarioBehaviour().get(0);
	}

	@Override
	public AbstractUserAction findNextAction(final UsageScenario scenario, final AbstractUserAction currentPosition) {
		final UsageScenario usageScenario = findUsageScenarioBy(scenario.getId());

		final EList<AbstractUserAction> actions = usageScenario.getScenarioBehaviour_UsageScenario()
		        .getActions_ScenarioBehaviour();
		for (final AbstractUserAction action : actions) {
			if (action.getId().equals(currentPosition.getId())) {
				return action.getSuccessor();
			}
		}
		// no more actions available
		return null;
	}

	public UsageScenario findUsageScenarioBy(final String scenarioId) {
		final EList<UsageScenario> usageScenarios = usageModel.getUsageScenario_UsageModel();
		for (final UsageScenario usageScenario : usageScenarios) {
			if (usageScenario.getId().equals(scenarioId)) {
				return usageScenario;
			}
		}
		// FIXME: throw Exception if scenario not found
		return null;
	}

}
