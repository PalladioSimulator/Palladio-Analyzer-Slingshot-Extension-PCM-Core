package org.palladiosimulator.analyzer.slingshot.behavior.usagesimulation.interpreters;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.UserInterpretationContext;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.UserLoopContextHolder;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.UserRequest;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UserFinished;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UserInterpretationProgressed;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UserLoopInitiated;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UserRequestInitiated;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UserSlept;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UserStarted;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UserWokeUp;
import org.palladiosimulator.analyzer.slingshot.common.utils.TransitionDeterminer;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.pcm.parameter.VariableUsage;
import org.palladiosimulator.pcm.repository.OperationProvidedRole;
import org.palladiosimulator.pcm.repository.OperationSignature;
import org.palladiosimulator.pcm.usagemodel.AbstractUserAction;
import org.palladiosimulator.pcm.usagemodel.Branch;
import org.palladiosimulator.pcm.usagemodel.BranchTransition;
import org.palladiosimulator.pcm.usagemodel.Delay;
import org.palladiosimulator.pcm.usagemodel.EntryLevelSystemCall;
import org.palladiosimulator.pcm.usagemodel.Loop;
import org.palladiosimulator.pcm.usagemodel.ScenarioBehaviour;
import org.palladiosimulator.pcm.usagemodel.Start;
import org.palladiosimulator.pcm.usagemodel.Stop;
import org.palladiosimulator.pcm.usagemodel.UsageScenario;
import org.palladiosimulator.pcm.usagemodel.util.UsagemodelSwitch;

import de.uka.ipd.sdq.simucomframework.variables.StackContext;

/**
 * The usage scenario interpreter interprets a single usage scenario. In order
 * for it to work, it needs the user instance and the user context.
 * 
 * @author Julijan Katic
 */
public class UsageScenarioInterpreter extends UsagemodelSwitch<Set<DESEvent>> {

	private final Logger LOGGER = Logger.getLogger(UsageScenarioInterpreter.class);

	/** The context from which the user needs to be interpreted. */
	private final UserInterpretationContext userContext;

	/**
	 * Instantiates the UsageScenarioInterpreter.
	 * 
	 * @param user        The user using the system.
	 * @param userContext The context of the user holding further information.
	 */
	public UsageScenarioInterpreter(final UserInterpretationContext userContext) {
		super();
		this.userContext = userContext;
	}

	/**
	 * This will handle the loop action by evaluating the number of loops that
	 * should happen and returning a set of {@link UserLoopInitiated} event. The
	 * UserInterpretationContext will receive a {@link UserLoopContextHolder} which
	 * gives knowledge about the current loop count and the number of loops needed.
	 * 
	 * @return set of {@link UserLoopInitiated} event.
	 */
	@Override
	public Set<DESEvent> caseLoop(final Loop loop) {
		final int numberOfLoops = StackContext.evaluateStatic(loop.getLoopIteration_Loop().getSpecification(), Integer.class);
		LOGGER.debug("Interpret loop. Maximum loop number: " + numberOfLoops);
		final ScenarioBehaviour bodyBehavior = loop.getBodyBehaviour_Loop();
		final Start loopStartAction = (Start) bodyBehavior.getActions_ScenarioBehaviour().get(0);

		final UserLoopContextHolder loopContext = UserLoopContextHolder.builder()
		        .withNumberOfLoops(numberOfLoops)
		        .withProgression(0)
		        .withLoopStartAction(loopStartAction)
		        .withAfterLoopAction(loop.getSuccessor())
		        .build();

		final UserInterpretationContext childInterpretationContext = userContext.update()
		        .withCurrentAction(loopStartAction)
		        .withLoopContext(loopContext)
		        .withParentContext(userContext)
		        .build();

		return Set.of(new UserLoopInitiated(childInterpretationContext));
	}

	/**
	 * Interprets the EntryLevelSystemCall of the usage model. This will result in a
	 * {@link UserRequestInitiated} event with the appropriate {@link UserRequest}
	 * entity.
	 * 
	 * @return set with {@link UserRequestInitiated} event.
	 */
	@Override
	public Set<DESEvent> caseEntryLevelSystemCall(final EntryLevelSystemCall object) {
		LOGGER.debug("Entering EntryLevelSystemCall");

		final OperationProvidedRole opProvidedRole = EcoreUtil.copy(object.getProvidedRole_EntryLevelSystemCall());
		final OperationSignature signature = EcoreUtil.copy(object.getOperationSignature__EntryLevelSystemCall());
		final EList<VariableUsage> inputParameterUsages = object.getInputParameterUsages_EntryLevelSystemCall();

		final UserRequest userRequest = new UserRequest(userContext.getUser(), opProvidedRole, signature, inputParameterUsages);
		final UserRequestInitiated uRequestInitiated = new UserRequestInitiated(userRequest,
		        userContext.update().withCurrentAction(object.getSuccessor()).build(), 0);
		return Set.of(uRequestInitiated);
	}

	/**
	 * Interprets the Stop action and immediately returns the set with
	 * {@link UserFinished} event.
	 * 
	 * @return set with {@link UserFinished} event.
	 */
	@Override
	public Set<DESEvent> caseStop(final Stop object) {
		return Set.of(new UserFinished(userContext));
	}

	/**
	 * Interprets the Start action and immediately returns the set with
	 * {@link UserStarted} event.
	 * 
	 * @return set with {@link UserStarted} event.
	 */
	@Override
	public Set<DESEvent> caseStart(final Start object) {
		return Set.of(new UserStarted(userContext.update().withCurrentAction(object.getSuccessor()).build(),
		        this.userContext.getThinkTime()));
	}

	/**
	 * Interprets the branch action by randomly picking the branch transition in
	 * accordance to their branch probabilities. This will result in two events: The
	 * event that is caused by interpreting the first action inside the chosen
	 * branch transition, and a {@link UserInterpretationProgressed} event that is
	 * used to hold the action that comes after the whole branch action.
	 * 
	 * @return set of the events that are returned by the first action of the branch
	 *         transition, and {@link UserInterpretationProgressed}.
	 */
	@Override
	public Set<DESEvent> caseBranch(final Branch branch) {
		final TransitionDeterminer transitionDeterminer = new TransitionDeterminer(
		        this.userContext.getUser().getStack().currentStackFrame());
		final BranchTransition branchTransition = transitionDeterminer
		        .determineBranchTransition(branch.getBranchTransitions_Branch());

		final Set<DESEvent> events = new HashSet<>(
		        this.doSwitch(branchTransition.getBranchedBehaviour_BranchTransition()));
		events.add(new UserInterpretationProgressed(userContext.update().withCurrentAction(branch.getSuccessor()).build(),
		        userContext.getThinkTime()));

		return events;
	}

	/**
	 * Interprets the usage scenario. This will call the corresponding method within
	 * this interpreter for the scenario behavior.
	 */
	@Override
	public Set<DESEvent> caseUsageScenario(final UsageScenario object) {
		return this.doSwitch(object.getScenarioBehaviour_UsageScenario());
	}

	@Override
	public Set<DESEvent> caseAbstractUserAction(final AbstractUserAction object) {
		LOGGER.debug("Interpret " + object.eClass().getName() + ": " + object);
		return Set.of();
	}

	/**
	 * Interprets the start action of the scenario behavior. This will only
	 * interpret the Start action and result in events if the first action of the
	 * scenario behavior is a Start action. If it is not the case, then an empty set
	 * will be returned.
	 * 
	 * @return set of events by interpreting the first action within the behavior if
	 *         it is a start action, otherwise an empty set.
	 */
	@Override
	public Set<DESEvent> caseScenarioBehaviour(final ScenarioBehaviour object) {
		// interpret start user action
		for (final AbstractUserAction abstractUserAction : object.getActions_ScenarioBehaviour()) {
			if (abstractUserAction instanceof Start) {
				return this.doSwitch(abstractUserAction);
			}
		}
		return Set.of();
	}

	/**
	 * Interprets the delay of a user by resulting in two events: The
	 * {@link UserSlept} event will be first returned, and then the
	 * {@link UserWokeUp} with the delay will be returned.
	 * 
	 * @return set of the events {@link UserSlept} with no delay and
	 *         {@link UserWokeUp} with the delay specified.
	 */
	@Override
	public Set<DESEvent> caseDelay(final Delay object) {
		final double delay = StackContext.evaluateStatic(object.getTimeSpecification_Delay().getSpecification(),
		        Double.class);
		return Set.of(new UserSlept(userContext.update().withCurrentAction(object.getSuccessor()).build()),
		        new UserWokeUp(userContext.update().withCurrentAction(object.getSuccessor()).build(), delay));
	}

	/**
	 * Performs the switch on the object an ensures that always an instance is
	 * returned, but never {@code null}. If the {@code doSwitch} results in a
	 * {@code null} reference, then an empty set is returned instead.
	 * 
	 * @return a set or an empty set, if the original method resulted in
	 *         {@code null}.
	 */
	@Override
	public Set<DESEvent> doSwitch(final EObject eObject) {
		if (eObject == null) {
			return Set.of();
		}
		final Set<DESEvent> returningEvents = super.doSwitch(eObject);
		if (returningEvents == null) {
			return Set.of();
		} else {
			return returningEvents;
		}
	}

}
