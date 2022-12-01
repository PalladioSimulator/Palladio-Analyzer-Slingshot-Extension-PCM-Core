package org.palladiosimulator.analyzer.slingshot.behavior.usagesimulation.interpreters;

import java.util.Optional;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.UserRequest;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.interpretationcontext.ClosedWorkloadUserInterpretationContext;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.interpretationcontext.OpenWorkloadUserInterpretationContext;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.interpretationcontext.UserInterpretationContext;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.scenariobehavior.BranchScenarioContext;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.scenariobehavior.LoopScenarioBehaviorContext;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.scenariobehavior.UsageScenarioBehaviorContext;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.InnerScenarioBehaviorInitiated;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.InterArrivalUserInitiated;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UserEntryRequested;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UserFinished;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UserInterpretationProgressed;
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
	 * UserInterpretationContext will receive a
	 * {@link UserLoopInterpretationContext} which gives knowledge about the current
	 * loop count and the number of loops needed.
	 * 
	 * @return set of {@link UserLoopInitiated} event.
	 */
	@Override
	public Set<DESEvent> caseLoop(final Loop loop) {
		final int numberOfLoops = StackContext.evaluateStatic(loop.getLoopIteration_Loop().getSpecification(),
				Integer.class);
		this.LOGGER.debug("Interpret loop. Maximum loop number: " + numberOfLoops);
		final ScenarioBehaviour bodyBehavior = loop.getBodyBehaviour_Loop();

		final AbstractUserAction firstLoopAction = bodyBehavior.getActions_ScenarioBehaviour().get(0);
		assert firstLoopAction instanceof Start;

		final UsageScenarioBehaviorContext behaviorContext = LoopScenarioBehaviorContext.builder()
				.withNextAction(Optional.of(loop.getSuccessor()))
				.withParent(Optional.of(this.userContext.getBehaviorContext()))
				.withReferencedContext(this.userContext.update().withCurrentAction(firstLoopAction)
						.withParentContext(Optional.of(this.userContext)).build())
				.withScenarioBehavior(bodyBehavior).withMaximalLoopCount(numberOfLoops).build();

		final InnerScenarioBehaviorInitiated behaviorInitiated = new InnerScenarioBehaviorInitiated(
				behaviorContext.getReferencedContext(), 0);

		return Set.of(behaviorInitiated);
	}

	/**
	 * Interprets the EntryLevelSystemCall of the usage model. This will result in a
	 * {@link UserRequestInitiated} event with the appropriate {@link UserRequest}
	 * entity.
	 * 
	 * @return set with {@link UserEntryRequested} event.
	 */
	@Override
	public Set<DESEvent> caseEntryLevelSystemCall(final EntryLevelSystemCall object) {
		this.LOGGER.debug("Entering EntryLevelSystemCall");

		final OperationProvidedRole opProvidedRole = EcoreUtil.copy(object.getProvidedRole_EntryLevelSystemCall());
		final OperationSignature signature = EcoreUtil.copy(object.getOperationSignature__EntryLevelSystemCall());
		final EList<VariableUsage> inputParameterUsages = object.getInputParameterUsages_EntryLevelSystemCall();

		final UserRequest userRequest = UserRequest.builder().withUser(this.userContext.getUser())
				.withOperationProvidedRole(opProvidedRole).withOperationSignature(signature)
				.withVariableUsages(inputParameterUsages).build();

		final UserEntryRequested userEntryRequest = new UserEntryRequested(userRequest,
				this.userContext.update().withCurrentAction(object.getSuccessor()).build(), 0);

		return Set.of(userEntryRequest);
	}

	/**
	 * Interprets the Stop action and immediately returns the set with
	 * {@link UserFinished} event.
	 * 
	 * @return set with {@link UserFinished} event.
	 */
	@Override
	public Set<DESEvent> caseStop(final Stop object) {
		return Set.of(new UserFinished(this.userContext));
	}

	/**
	 * Interprets the Start action and immediately returns the set with
	 * {@link UserStarted} event. If this is in a nested context, then only
	 * {@link UserStarted} will be returned.
	 * 
	 * @return set with {@link UserStarted} event, and if it is an open workload
	 *         user, then also a {@link InterArrivalUserInitiated} event to start a
	 *         new user after a specified interArrivalTime.
	 */
	@Override
	public Set<DESEvent> caseStart(final Start object) {
		final Set<DESEvent> resultSet;

		if (this.userContext.getBehaviorContext().isChildContext()) {
			resultSet = Set.of(new UserStarted(this.userContext.updateAction(object.getSuccessor()), 0));
		} else if (this.userContext instanceof ClosedWorkloadUserInterpretationContext) {
			final double thinkTime = ((ClosedWorkloadUserInterpretationContext) this.userContext).getThinkTime()
					.calculateRV();
			resultSet = Set.of(new UserStarted(this.userContext.updateAction(object.getSuccessor()), thinkTime));
		} else if (this.userContext instanceof OpenWorkloadUserInterpretationContext) {
			final double interArrivalTime = ((OpenWorkloadUserInterpretationContext) this.userContext)
					.getInterArrivalTime().calculateRV();
			resultSet = Set.of(new UserStarted(this.userContext.updateAction(object.getSuccessor())),
					new InterArrivalUserInitiated(interArrivalTime));
		} else {
			this.LOGGER.info("The user is neither a closed workload nor open workload user");
			throw new IllegalStateException("The user must be a open workload or closed workload user");
		}
		return resultSet;
	}

	/**
	 * Interprets the branch action by randomly picking the branch transition in
	 * accordance to their branch probabilities. This will result in two events: The
	 * event that is caused by interpreting the first action inside the chosen
	 * branch transition, and a {@link UserInterpretationProgressed} event that is
	 * used to hold the action that comes after the whole branch action.
	 * 
	 * @return set of the events that are returned by the first action of the branch
	 *         transition, and {@link innerScenarioBehaviorInitiated}.
	 */
	@Override
	public Set<DESEvent> caseBranch(final Branch branch) {
		final TransitionDeterminer transitionDeterminer = new TransitionDeterminer(
				this.userContext.getUser().getStack().currentStackFrame());
		final BranchTransition branchTransition = transitionDeterminer
				.determineBranchTransition(branch.getBranchTransitions_Branch());

		final AbstractUserAction firstBranchAction = branchTransition.getBranchedBehaviour_BranchTransition()
				.getActions_ScenarioBehaviour().get(0);
		assert firstBranchAction instanceof Start;

		final UsageScenarioBehaviorContext behaviorContext = BranchScenarioContext.builder()
				.withNextAction(Optional.of(branch.getSuccessor()))
				.withParent(Optional.of(this.userContext.getBehaviorContext()))
				.withReferencedContext(this.userContext.update().withCurrentAction(firstBranchAction).build())
				.withScenarioBehavior(branchTransition.getBranchedBehaviour_BranchTransition()).build();

		final InnerScenarioBehaviorInitiated innerScenarioBehaviorInitiated = new InnerScenarioBehaviorInitiated(
				behaviorContext.getReferencedContext(), 0);

		return Set.of(innerScenarioBehaviorInitiated);
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
		this.LOGGER.debug("Interpret " + object.eClass().getName() + ": " + object);
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
		final UserInterpretationContext updatedUserContext = this.userContext.updateAction(object.getSuccessor());
		return Set.of(new UserSlept(updatedUserContext), new UserWokeUp(updatedUserContext, delay));
	}

	/**
	 * Performs the switch on the object an ensures that always an instance is
	 * returned, but never {@code null}. If the {@code doSwitch} results in a
	 * {@code null} reference, then an empty set is returned instead.
	 * 
	 * @return a set or an empty set, if the original method resulted in
	 *         {@code null}.
	 * @throws IllegalArgumentException if this was called with a {@code null}
	 *                                  reference.
	 */
	@Override
	public Set<DESEvent> doSwitch(final EObject eObject) {
		if (eObject == null) {
			throw new IllegalArgumentException("called interpretation on a null reference");
		}
		final Set<DESEvent> returningEvents = super.doSwitch(eObject);
		if (returningEvents == null) {
			return Set.of();
		} else {
			return returningEvents;
		}
	}

}
