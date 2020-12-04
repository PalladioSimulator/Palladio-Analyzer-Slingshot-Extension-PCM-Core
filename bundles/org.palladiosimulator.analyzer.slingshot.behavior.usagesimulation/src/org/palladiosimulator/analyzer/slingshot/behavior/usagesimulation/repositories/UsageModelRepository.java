package org.palladiosimulator.analyzer.slingshot.behavior.usagesimulation.repositories;

import java.util.List;

import org.palladiosimulator.pcm.usagemodel.AbstractUserAction;
import org.palladiosimulator.pcm.usagemodel.ClosedWorkload;
import org.palladiosimulator.pcm.usagemodel.UsageModel;
import org.palladiosimulator.pcm.usagemodel.UsageScenario;
import org.palladiosimulator.pcm.usagemodel.Workload;

import com.google.inject.ImplementedBy;

/**
 * A usage model repository gives further, non-traversal access to the usage
 * model. Unlike a Interpreter, a repository can be used to make random access
 * to the model instead.
 * 
 * @author Julijan Katic
 */
@ImplementedBy(UsageModelRepositoryImpl.class)
public interface UsageModelRepository {

	void load(UsageModel usageModel);

	List<UsageScenario> findAllUsageScenarios();

	Workload findWorkloadForUsageScenario(final UsageScenario usageScenario);

	int findClosedWorkloadPopulation(final ClosedWorkload workload);

	AbstractUserAction findFirstActionOf(final UsageScenario usageScenario);

	AbstractUserAction findNextAction(UsageScenario scenario, AbstractUserAction currentPosition);

}
