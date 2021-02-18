package org.palladiosimulator.analyzer.slingshot.behavior.usagesimulation.interpreters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.util.Set;

import org.eclipse.emf.common.util.ECollections;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.ThinkTime;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.User;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.interpretationcontext.ClosedWorkloadUserInterpretationContext;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.scenariobehavior.BranchScenarioContext;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.scenariobehavior.LoopScenarioBehaviorContext;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.scenariobehavior.UsageScenarioBehaviorContext;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.InnerScenarioBehaviorInitiated;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UserEntryRequested;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UserSlept;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UserStarted;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UserWokeUp;
import org.palladiosimulator.analyzer.slingshot.common.utils.SimulatedStackHelper;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.pcm.core.CoreFactory;
import org.palladiosimulator.pcm.core.PCMRandomVariable;
import org.palladiosimulator.pcm.repository.OperationProvidedRole;
import org.palladiosimulator.pcm.repository.OperationSignature;
import org.palladiosimulator.pcm.repository.RepositoryFactory;
import org.palladiosimulator.pcm.usagemodel.Branch;
import org.palladiosimulator.pcm.usagemodel.BranchTransition;
import org.palladiosimulator.pcm.usagemodel.Delay;
import org.palladiosimulator.pcm.usagemodel.EntryLevelSystemCall;
import org.palladiosimulator.pcm.usagemodel.Loop;
import org.palladiosimulator.pcm.usagemodel.ScenarioBehaviour;
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
	private ClosedWorkloadUserInterpretationContext.Builder builder;
	private UsageScenarioInterpreter interpreter;

	private static final UsagemodelFactory usageModelFactory = UsagemodelFactory.eINSTANCE;
	private static final CoreFactory coreFactory = CoreFactory.eINSTANCE;
	private static final RepositoryFactory repositoryFactory = RepositoryFactory.eINSTANCE;

	@BeforeAll
	public static void initializeCache() {
		final IProbabilityFunctionFactory probabilityFunctionFactory = ProbabilityFunctionFactoryImpl.getInstance();
		StoExCache.initialiseStoExCache(probabilityFunctionFactory);
	}

	@BeforeEach
	public void initializeTest() {
		MockitoAnnotations.initMocks(this);

		this.builder = ClosedWorkloadUserInterpretationContext.builder();

		final UsageScenarioBehaviorContext behaviorContext = mock(UsageScenarioBehaviorContext.class);
		final User user = new User();

		SimulatedStackHelper.createAndPushNewStackFrame(user.getStack(), ECollections.emptyEList());

		this.interpretationContext = this.builder.withUsageScenarioBehaviorContext(behaviorContext).withUser(user)
				.build();
		this.interpreter = spy(new UsageScenarioInterpreter(this.interpretationContext));
	}

	@Test
	public void test_startAction() {
		final PCMRandomVariable thinkTimeRV = coreFactory.createPCMRandomVariable();
		thinkTimeRV.setSpecification("0");

		final Start startEntity = usageModelFactory.createStart();
		startEntity.setEntityName("start");
		startEntity.setSuccessor(startEntity);

		final ThinkTime thinkTime = new ThinkTime(thinkTimeRV);

		this.interpreter = spy(
				new UsageScenarioInterpreter(this.interpretationContext.update().withThinkTime(thinkTime).build()));

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

	@Test
	public void test_entryLevelSystemCallAction() {
		final EntryLevelSystemCall entryLevelSystemCall = usageModelFactory.createEntryLevelSystemCall();
		entryLevelSystemCall.setEntityName("entry_level_system_call");

		final OperationSignature operationSignature = repositoryFactory.createOperationSignature();
		entryLevelSystemCall.setOperationSignature__EntryLevelSystemCall(operationSignature);

		final OperationProvidedRole operationProvidedRole = repositoryFactory.createOperationProvidedRole();
		entryLevelSystemCall.setProvidedRole_EntryLevelSystemCall(operationProvidedRole);

		final Set<DESEvent> returnedEvents = this.interpreter.doSwitch(entryLevelSystemCall);
		verify(this.interpreter).caseEntryLevelSystemCall(entryLevelSystemCall);

		assertEquals(1, returnedEvents.size());
		assertTrue(returnedEvents.stream().allMatch(UserEntryRequested.class::isInstance));

		final UserEntryRequested userEntryRequested = (UserEntryRequested) returnedEvents.iterator().next();

		assertEquals(operationSignature.getId(), userEntryRequested.getEntity().getOperationSignature().getId());
		assertEquals(operationProvidedRole.getId(), userEntryRequested.getEntity().getOperationProvidedRole().getId());
	}

	@Test
	public void test_branchAction() {
		final Branch branchAction = usageModelFactory.createBranch();

		final BranchTransition branchTransition1 = usageModelFactory.createBranchTransition();
		branchTransition1.setBranch_BranchTransition(branchAction);
		branchTransition1.setBranchProbability(0.5D);
		branchTransition1.setBranchedBehaviour_BranchTransition(this.innerScenarioBuilder());

		final BranchTransition branchTransition2 = usageModelFactory.createBranchTransition();
		branchTransition2.setBranch_BranchTransition(branchAction);
		branchTransition2.setBranchProbability(0.5D);
		branchTransition2.setBranchedBehaviour_BranchTransition(this.innerScenarioBuilder());

		branchAction.getBranchTransitions_Branch().add(branchTransition1);
		branchAction.getBranchTransitions_Branch().add(branchTransition2);
		branchAction.setSuccessor(branchAction);

		final Set<DESEvent> returnedEvents = this.interpreter.doSwitch(branchAction);
		verify(this.interpreter).caseBranch(branchAction);

		assertEquals(1, returnedEvents.size());
		assertTrue(returnedEvents.stream().allMatch(InnerScenarioBehaviorInitiated.class::isInstance));

		final InnerScenarioBehaviorInitiated event = (InnerScenarioBehaviorInitiated) returnedEvents.iterator().next();

		assertTrue(event.getEntity().getBehaviorContext() instanceof BranchScenarioContext);
	}

	@Test
	public void test_LoopAction() {
		final Loop loopAction = usageModelFactory.createLoop();
		final PCMRandomVariable loopIteration = coreFactory.createPCMRandomVariable();

		loopIteration.setSpecification("1");

		loopAction.setLoopIteration_Loop(loopIteration);

		final ScenarioBehaviour scenarioBehavior = this.innerScenarioBuilder();
		scenarioBehavior.setLoop_ScenarioBehaviour(loopAction);
		loopAction.setBodyBehaviour_Loop(scenarioBehavior);

		loopAction.setSuccessor(loopAction);

		final Set<DESEvent> returnedEvents = this.interpreter.doSwitch(loopAction);
		verify(this.interpreter).caseLoop(loopAction);

		assertEquals(1, returnedEvents.size());
		assertTrue(returnedEvents.stream().allMatch(InnerScenarioBehaviorInitiated.class::isInstance));
		final InnerScenarioBehaviorInitiated event = (InnerScenarioBehaviorInitiated) returnedEvents.iterator().next();
		assertTrue(event.getEntity().getBehaviorContext() instanceof LoopScenarioBehaviorContext);
	}

	@Test
	public void test_callInterpreterWithNull() {
		assertThrows(IllegalArgumentException.class, () -> {
			this.interpreter.doSwitch(null);
		});
	}

	private ScenarioBehaviour innerScenarioBuilder() {
		final ScenarioBehaviour scenarioBehavior = usageModelFactory.createScenarioBehaviour();
		final Start start = usageModelFactory.createStart();
		start.setScenarioBehaviour_AbstractUserAction(scenarioBehavior);
		scenarioBehavior.getActions_ScenarioBehaviour().add(start);
		return scenarioBehavior;
	}
}
