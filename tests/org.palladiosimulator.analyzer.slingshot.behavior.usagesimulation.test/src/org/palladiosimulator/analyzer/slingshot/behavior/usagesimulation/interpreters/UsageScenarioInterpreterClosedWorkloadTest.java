package org.palladiosimulator.analyzer.slingshot.behavior.usagesimulation.interpreters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.ThinkTime;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.interpretationcontext.ClosedWorkloadUserInterpretationContext;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UserSlept;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UserStarted;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UserWokeUp;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.pcm.core.CoreFactory;
import org.palladiosimulator.pcm.core.PCMRandomVariable;
import org.palladiosimulator.pcm.usagemodel.AbstractUserAction;
import org.palladiosimulator.pcm.usagemodel.Delay;
import org.palladiosimulator.pcm.usagemodel.Start;
import org.palladiosimulator.pcm.usagemodel.UsagemodelFactory;

import de.uka.ipd.sdq.probfunction.math.IProbabilityFunctionFactory;
import de.uka.ipd.sdq.probfunction.math.impl.ProbabilityFunctionFactoryImpl;
import de.uka.ipd.sdq.simucomframework.variables.cache.StoExCache;

/**
 * This tests the {@link UsageScenarioInterpreter} class by mocking the required
 * contexts especially by using the closed workload user.
 * 
 * @author Julijan Katic
 */
public class UsageScenarioInterpreterClosedWorkloadTest {

	private ClosedWorkloadUserInterpretationContext interpretationContext;
	private UsageScenarioInterpreter interpreter;

	private static final UsagemodelFactory usageModelFactory = UsagemodelFactory.eINSTANCE;
	private static final CoreFactory coreFactory = CoreFactory.eINSTANCE;

	@BeforeAll
	public static void initializeCache() {
		final IProbabilityFunctionFactory probabilityFunctionFactory = ProbabilityFunctionFactoryImpl.getInstance();
		StoExCache.initialiseStoExCache(probabilityFunctionFactory);
	}

	@BeforeEach
	public void initializeTest() {
		MockitoAnnotations.initMocks(this);
		this.interpretationContext = mock(ClosedWorkloadUserInterpretationContext.class);
		this.interpreter = spy(new UsageScenarioInterpreter(this.interpretationContext));

		when(this.interpretationContext.updateAction(any(AbstractUserAction.class)))
				.thenReturn(this.interpretationContext);
	}

	@Test
	public void test_startAction() {
		final PCMRandomVariable thinkTimeRV = coreFactory.createPCMRandomVariable();
		thinkTimeRV.setSpecification("0");

		final Start startEntity = usageModelFactory.createStart();
		startEntity.setEntityName("start");
		startEntity.setSuccessor(startEntity);

		final ThinkTime thinkTime = new ThinkTime(thinkTimeRV);

		when(this.interpretationContext.getThinkTime()).thenReturn(thinkTime);

		final Set<DESEvent> returnedEvents = this.interpreter.doSwitch(startEntity);

		// This method must have been called.
		verify(this.interpreter).caseStart(startEntity);

		assertEquals(1, returnedEvents.size());
		assertTrue(returnedEvents.stream().allMatch(UserStarted.class::isInstance));

		final UserStarted userStarted = (UserStarted) returnedEvents.iterator().next();

		assertEquals(0.0, userStarted.time());
	}

	@Test
	public void test_delayAction() {
		final PCMRandomVariable delayRV = coreFactory.createPCMRandomVariable();
		delayRV.setSpecification("1");

		final Delay delayEntity = usageModelFactory.createDelay();
		delayEntity.setEntityName("delay");
		delayEntity.setSuccessor(delayEntity);
		delayEntity.setTimeSpecification_Delay(delayRV);

		final Set<DESEvent> returnedEvents = this.interpreter.doSwitch(delayEntity);

		verify(this.interpreter).caseDelay(delayEntity);

		assertEquals(2, returnedEvents.size());
		assertTrue(returnedEvents.stream().anyMatch(UserSlept.class::isInstance));
		assertTrue(returnedEvents.stream().anyMatch(UserWokeUp.class::isInstance));

		final UserWokeUp userWokeUp = (UserWokeUp) returnedEvents.stream().filter(UserWokeUp.class::isInstance)
				.findFirst().get();

		assertEquals(1, userWokeUp.getDelay());
	}
}
