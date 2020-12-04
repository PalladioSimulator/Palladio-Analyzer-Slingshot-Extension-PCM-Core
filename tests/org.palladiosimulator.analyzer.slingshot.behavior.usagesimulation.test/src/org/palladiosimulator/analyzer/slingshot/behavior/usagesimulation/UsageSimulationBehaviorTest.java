package org.palladiosimulator.analyzer.slingshot.behavior.usagesimulation;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UserStarted;
import org.palladiosimulator.analyzer.slingshot.behavior.usagesimulation.repositories.UsageModelRepository;
import org.palladiosimulator.analyzer.slingshot.simulation.core.events.SimulationStarted;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.results.ResultEvent;
import org.palladiosimulator.pcm.usagemodel.UsageModel;

public class UsageSimulationBehaviorTest {

	@Mock
	private UsageModelRepository usageModelRepository;

	private UsageSimulationBehavior usageSimulationBehavior;

	@Mock
	private UsageModel usageModel;

	@BeforeEach
	public void setUpEventBus() {
		this.usageSimulationBehavior = new UsageSimulationBehavior(usageModel, usageModelRepository);
	}

	@Test
	public void testSimulationStartedOnClosedWorkload() {
		final SimulationStarted simulationStarted = mock(SimulationStarted.class);

		final ResultEvent<DESEvent> result = this.usageSimulationBehavior.onSimulationStart(simulationStarted);

		assertTrue(result.getEventsForScheduling().stream().anyMatch(event -> event.getClass().equals(UserStarted.class)));
	}
}
