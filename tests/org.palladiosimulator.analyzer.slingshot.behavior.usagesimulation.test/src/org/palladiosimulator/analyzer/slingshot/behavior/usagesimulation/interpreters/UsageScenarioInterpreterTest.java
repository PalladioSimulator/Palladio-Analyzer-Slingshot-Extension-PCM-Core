package org.palladiosimulator.analyzer.slingshot.behavior.usagesimulation.interpreters;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.UserInterpretationContext;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UserStarted;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.pcm.usagemodel.Start;

public class UsageScenarioInterpreterTest {

	private UsageScenarioInterpreter interpreter;

	@Mock
	private UserInterpretationContext context;

	@BeforeEach
	public void setUpInterpreter() {
		interpreter = new UsageScenarioInterpreter(context);
	}

	@Test
	public void testStartAction() {
		final Start startAction = mock(Start.class);

		when(startAction.getSuccessor()).thenReturn(startAction);
		when(context.getThinkTime()).thenReturn(0.0);

		final Set<DESEvent> result = interpreter.caseStart(startAction);
		verify(context).getThinkTime();

		assertTrue(result.size() == 1);
		assertTrue(result.stream().findAny().isPresent());
		assertTrue(result.stream().findFirst().get().getClass().getSimpleName().equals(UserStarted.class.getSimpleName()));
	}

}
