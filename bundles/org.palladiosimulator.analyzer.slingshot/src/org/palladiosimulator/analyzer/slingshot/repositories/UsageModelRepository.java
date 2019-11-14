package org.palladiosimulator.analyzer.slingshot.repositories;

import java.util.List;

import org.palladiosimulator.pcm.usagemodel.AbstractUserAction;
import org.palladiosimulator.pcm.usagemodel.ClosedWorkload;
import org.palladiosimulator.pcm.usagemodel.UsageModel;
import org.palladiosimulator.pcm.usagemodel.UsageScenario;
import org.palladiosimulator.pcm.usagemodel.Workload;

public interface UsageModelRepository {
	
	void load(UsageModel usageModel);

	List<UsageScenario> findAllUsageScenarios();
	 
	Workload findWorkloadForUsageScenario(final UsageScenario usageScenario);
	
	int findClosedWorkloadPopulation(final ClosedWorkload workload);
	
	AbstractUserAction findFirstActionOf(final UsageScenario usageScenario);

	AbstractUserAction findNextAction(UsageScenario scenario, AbstractUserAction currentPosition);
	
}
