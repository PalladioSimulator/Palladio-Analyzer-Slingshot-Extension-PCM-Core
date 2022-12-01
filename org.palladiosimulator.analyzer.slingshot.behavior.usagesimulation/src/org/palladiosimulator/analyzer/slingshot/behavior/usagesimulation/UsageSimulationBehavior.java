package org.palladiosimulator.analyzer.slingshot.behavior.usagesimulation;

<<<<<<< HEAD
import static org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.eventcontract.EventCardinality.MANY;
=======
import static org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.EventCardinality.MANY;
>>>>>>> 6cc4c84 (prepare merge)

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.InterArrivalTime;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.ThinkTime;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.User;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.interpretationcontext.ClosedWorkloadUserInterpretationContext;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.interpretationcontext.OpenWorkloadUserInterpretationContext;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.interpretationcontext.UsageInterpretationContext;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.interpretationcontext.UsageScenarioInterpretationContext;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.interpretationcontext.UserInterpretationContext;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.scenariobehavior.RootScenarioContext;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.scenariobehavior.UsageScenarioBehaviorContext;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.ClosedWorkloadUserInitiated;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.InnerScenarioBehaviorInitiated;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.InterArrivalUserInitiated;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UsageModelPassedElement;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UsageScenarioFinished;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UsageScenarioStarted;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UserEntryRequested;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UserFinished;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UserRequestFinished;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UserSlept;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UserStarted;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UserWokeUp;
import org.palladiosimulator.analyzer.slingshot.behavior.usagesimulation.interpreters.UsageScenarioInterpreter;
import org.palladiosimulator.analyzer.slingshot.behavior.usagesimulation.repositories.UsageModelRepository;
<<<<<<< HEAD
//import org.palladiosimulator.analyzer.slingshot.common.utils.Postconditions;
import org.palladiosimulator.analyzer.slingshot.core.events.SimulationStarted;
import org.palladiosimulator.analyzer.slingshot.common.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.core.extension.SimulationBehaviorExtension;
import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.eventcontract.OnEvent;
import org.palladiosimulator.analyzer.slingshot.eventdriver.returntypes.Result;
import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.Subscribe;
=======
import org.palladiosimulator.analyzer.slingshot.common.utils.Postconditions;
import org.palladiosimulator.analyzer.slingshot.simulation.core.events.SimulationStarted;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.SimulationBehaviorExtension;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.OnEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.results.ResultEvent;
>>>>>>> 6cc4c84 (prepare merge)
import org.palladiosimulator.pcm.core.PCMRandomVariable;
import org.palladiosimulator.pcm.usagemodel.AbstractUserAction;
import org.palladiosimulator.pcm.usagemodel.ClosedWorkload;
import org.palladiosimulator.pcm.usagemodel.OpenWorkload;
import org.palladiosimulator.pcm.usagemodel.UsageModel;
import org.palladiosimulator.pcm.usagemodel.UsageScenario;

import com.google.common.collect.ImmutableList;
<<<<<<< HEAD
=======
import com.google.common.eventbus.Subscribe;
>>>>>>> 6cc4c84 (prepare merge)

/**
 * This behavior handles the events for the usage simulation.
 * 
 * It interprets the usage model by listening on the
 * {@link UsageInterpretationEvent}s. See the method documentation for further
 * information about the event handling.
 * 
 * @author Julijan Katic
 */
@OnEvent(when = SimulationStarted.class, then = { UserStarted.class,
		InterArrivalUserInitiated.class, UsageModelPassedElement.class }, cardinality = MANY)
@OnEvent(when = UserStarted.class, then = { UserFinished.class, UserEntryRequested.class, UserSlept.class,
		UserWokeUp.class, InnerScenarioBehaviorInitiated.class, UsageScenarioStarted.class,
		UsageModelPassedElement.class }, cardinality = MANY)
@OnEvent(when = UserFinished.class, then = { UserStarted.class, InterArrivalUserInitiated.class,
		ClosedWorkloadUserInitiated.class, UsageScenarioFinished.class, UserFinished.class, UserSlept.class,
		UserWokeUp.class, UsageModelPassedElement.class }, cardinality = MANY)
@OnEvent(when = UserWokeUp.class, then = { UserFinished.class, UserEntryRequested.class, UserSlept.class,
		UserWokeUp.class, InnerScenarioBehaviorInitiated.class, UsageModelPassedElement.class }, cardinality = MANY)
@OnEvent(when = UserRequestFinished.class, then = { UserFinished.class, UserEntryRequested.class, UserSlept.class,
		UserWokeUp.class, InnerScenarioBehaviorInitiated.class, UsageModelPassedElement.class }, cardinality = MANY)
@OnEvent(when = InnerScenarioBehaviorInitiated.class, then = { UserStarted.class,
		UsageModelPassedElement.class }, cardinality = MANY)
@OnEvent(when = ClosedWorkloadUserInitiated.class, then = { UserStarted.class,
		UsageModelPassedElement.class }, cardinality = MANY)
<<<<<<< HEAD
@OnEvent(when = InterArrivalUserInitiated.class, then = { UserFinished.class, UserEntryRequested.class, UserSlept.class,
		UserWokeUp.class, InnerScenarioBehaviorInitiated.class, UsageScenarioStarted.class,
		UsageModelPassedElement.class }, cardinality = MANY)
=======
>>>>>>> 6cc4c84 (prepare merge)
public class UsageSimulationBehavior implements SimulationBehaviorExtension {

	private final Logger LOGGER = Logger.getLogger(UsageSimulationBehavior.class);

	private UsageInterpretationContext usageInterpretationContext;

	/** The repository for access into the usage model. */
	private final UsageModelRepository usageModelRepository;

	/** The model to interpret. */
	private final UsageModel usageModel;

	@Inject
	public UsageSimulationBehavior(final UsageModel usageModel, final UsageModelRepository repository) {
		this.usageModel = usageModel;
		this.usageModelRepository = repository;
<<<<<<< HEAD
		this.init();
	}

=======
	}

	@Override
>>>>>>> 6cc4c84 (prepare merge)
	public void init() {
		this.usageModelRepository.load(this.usageModel);
		this.usageInterpretationContext = UsageInterpretationContext.builder()
				.withUsageScenariosContexts(this.collectAllUsageScenarios())
				.build();
	}

	/**
	 * Helper method in order to collect all available usage scenarios and map them
	 * into the {@link UsageScenarioInterpretationContext}.
	 * 
	 * @return the immutable list of usage scenario contexts.
	 */
	private ImmutableList<UsageScenarioInterpretationContext> collectAllUsageScenarios() {
		return this.usageModelRepository.findAllUsageScenarios().stream()
				.map(scenario -> UsageScenarioInterpretationContext.builder().withScenario(scenario).build())
				.collect(Collectors.collectingAndThen(Collectors.toList(), ImmutableList::copyOf));
	}

	/**
	 * Handles the {@link SimulationStarted} event by starting the interpretation of
	 * the UsageModel. It will look up the workload, and depending on the workload,
	 * it will result in a {@link UsageInterpretationEvent}.
	 * 
	 * @return Set with {@link UsageInterpretationEvent}s.
	 */
	@Subscribe
<<<<<<< HEAD
	public Result onSimulationStart(final SimulationStarted evt) {
=======
	public ResultEvent<DESEvent> onSimulationStart(final SimulationStarted evt) {
>>>>>>> 6cc4c84 (prepare merge)
		final Set<DESEvent> returnedEvents = new HashSet<>();

		this.startUsageSimulation(returnedEvents);

		/*
		 * Because this is the first action, this should only contain the UserStarted
		 * and InterArrivalUserInitiated events.
		 */
<<<<<<< HEAD
		//assert Postconditions.checkResultTypesAndSize(returnedEvents,
		//		List.of(UserStarted.class, InterArrivalUserInitiated.class), 2);

		return Result.from(returnedEvents);
=======
		assert Postconditions.checkResultEventTypesAndSize(returnedEvents,
				List.of(UserStarted.class, InterArrivalUserInitiated.class), 2);

		return ResultEvent.ofAll(returnedEvents);
>>>>>>> 6cc4c84 (prepare merge)
	}

	/**
	 * This helper method is used in order to start the user simulation. Depending
	 * on the user's workload, the {@link UsageInterpretationEvent} will be in the
	 * set.
	 * 
	 * @param returnedEvents the set of events that should be published afterwards.
	 */
	private void startUsageSimulation(final Set<DESEvent> returnedEvents) {
		assert returnedEvents != null;

		for (final UsageScenarioInterpretationContext usageScenarioContext : this.usageInterpretationContext
				.getUsageScenarioContexts()) {
			final UsageScenario usageScenario = usageScenarioContext.getScenario();
			final AbstractUserAction firstAction = this.usageModelRepository.findFirstActionOf(usageScenario)
					.orElseThrow(() -> new IllegalStateException(
							"There must be a Start user action within the usage scenario."));

			if (usageScenarioContext.isClosedWorkload()) {
				this.interpreteClosedWorkload(returnedEvents, usageScenario, firstAction);
			} else {
				this.interpreteOpenWorkload(returnedEvents, usageScenario, firstAction);
			}
		}
	}

	/**
	 * Helper method for interpreting a usage scenario with an open workload. It
	 * will evaluate the specification for the number of users that will be
	 * processed.
	 * 
	 * @param returnedEvents The set of events into which the appeared events will
	 *                       be added.
	 * @param usageScenario  The scenario to interpret.
	 * @param firstAction    The first action in the scenario (typically a Start
	 *                       action).
	 */
	private void interpreteOpenWorkload(final Set<DESEvent> returnedEvents, final UsageScenario usageScenario,
			final AbstractUserAction firstAction) {
		assert usageScenario.getWorkload_UsageScenario() instanceof OpenWorkload;

		final OpenWorkload workloadSpec = (OpenWorkload) usageScenario.getWorkload_UsageScenario();
		final PCMRandomVariable interArrivalRV = workloadSpec.getInterArrivalTime_OpenWorkload();

		final RootScenarioContext scenarioContext = RootScenarioContext.builder()
				.withScenarioBehavior(usageScenario.getScenarioBehaviour_UsageScenario()).build();

		final OpenWorkloadUserInterpretationContext openWorkloadUserInterpretationContext = OpenWorkloadUserInterpretationContext
				.builder()
				.withUser(new User())
				.withScenario(usageScenario)
				.withCurrentAction(firstAction)
				.withInterArrivalTime(new InterArrivalTime(interArrivalRV))
				.withUsageScenarioBehaviorContext(scenarioContext)
				.build();

		final UsageScenarioInterpreter interpreter = new UsageScenarioInterpreter(
				openWorkloadUserInterpretationContext);

		final Set<DESEvent> events = interpreter.doSwitch(firstAction);

		assert events.size() == 2;
		assert events.stream().anyMatch(UserStarted.class::isInstance);
		assert events.stream().anyMatch(InterArrivalUserInitiated.class::isInstance);

		returnedEvents.addAll(events);
	}

	/**
	 * Helper method for interpreting a usage scenario with a closed workload. It
	 * will create {@link UsageInterpretionContext}s as specified in the workload.
	 * 
	 * @param returnedEvents The set of events into which the appeared events will
	 *                       be added.
	 * @param usageScenario  The scenario to interpret.
	 * @param firstAction    The first action in the scenario (typically a Start
	 *                       action).
	 */
	private void interpreteClosedWorkload(final Set<DESEvent> returnedEvents, final UsageScenario usageScenario,
			final AbstractUserAction firstAction) {
		assert usageScenario.getWorkload_UsageScenario() instanceof ClosedWorkload;

		final RootScenarioContext scenarioContext = RootScenarioContext.builder()
				.withScenarioBehavior(usageScenario.getScenarioBehaviour_UsageScenario())
				.build();

		final ClosedWorkload workloadSpec = (ClosedWorkload) usageScenario.getWorkload_UsageScenario();
		final ClosedWorkloadUserInterpretationContext.Builder partialInterpretationBuilder = ClosedWorkloadUserInterpretationContext
				.builder()
				.withCurrentAction(firstAction)
				.withThinkTime(new ThinkTime(workloadSpec.getThinkTime_ClosedWorkload()))
				.withUsageScenarioBehaviorContext(scenarioContext)
				.withScenario(usageScenario);

		for (int i = 0; i < workloadSpec.getPopulation(); i++) {
			final UserInterpretationContext interpretationContext = partialInterpretationBuilder
					.withUser(new User())
					.build();

			final UsageScenarioInterpreter interpreter = new UsageScenarioInterpreter(interpretationContext);
			final Set<DESEvent> events = interpreter.doSwitch(firstAction);

			assert events.size() == 1;
			assert events.stream().allMatch(UserStarted.class::isInstance);

			returnedEvents.addAll(events);
		}
	}

	/**
	 * Handles the UserStarted event by creating a new stack frame and interpreting
	 * the next action.
	 * 
	 * @return the set of events resulting from the interpretation of the next
	 *         action.
	 */
	@Subscribe
<<<<<<< HEAD
	public Result onUserStarted(final UserStarted userStarted) {
=======
	public ResultEvent<DESEvent> onUserStarted(final UserStarted userStarted) {
>>>>>>> 6cc4c84 (prepare merge)
		userStarted.getEntity().getUser().getStack().createAndPushNewStackFrame();
		final UsageScenarioInterpreter interpreter = new UsageScenarioInterpreter(userStarted.getEntity());
		final Set<DESEvent> result = new HashSet<>(interpreter.doSwitch(userStarted.getEntity().getCurrentAction()));

		/* If we are the root behavior, add this event for monitoring. */
		if (userStarted.getEntity().getParentContext().isEmpty()) {
			result.add(new UsageScenarioStarted(userStarted.getEntity(), 0));
		}

<<<<<<< HEAD
		return Result.from(result);
=======
		return ResultEvent.of(result);
>>>>>>> 6cc4c84 (prepare merge)
	}

	/**
	 * Creates a new set of open workload users for each user scenario. Returns the
	 * events of interpreting the first actions of each open workload user scenario.
	 * 
	 * @param interArrivalUserInitiated The event
	 * @return The events of interpreting the first actions in each scenario.
	 */
	@Subscribe
<<<<<<< HEAD
	public Result onInterArrivalUserInitiated(
=======
	public ResultEvent<DESEvent> onInterArrivalUserInitiated(
>>>>>>> 6cc4c84 (prepare merge)
			final InterArrivalUserInitiated interArrivalUserInitiated) {
		final Set<DESEvent> result = new HashSet<>();

		for (final UsageScenarioInterpretationContext usageScenarioContext : this.usageInterpretationContext
				.getUsageScenarioContexts()) {
			final AbstractUserAction firstAction = this.usageModelRepository
					.findFirstActionOf(usageScenarioContext.getScenario())
					.orElseThrow(
							() -> new IllegalStateException("There must be a Start action within the usage scenario."));

			this.interpreteOpenWorkload(result, usageScenarioContext.getScenario(), firstAction);
		}

		// The set should only contain UserStarted and InterArrivalUserInitiated events.
		assert result.stream()
				.filter(Predicate.not(UserStarted.class::isInstance))
				.filter(Predicate.not(InterArrivalUserInitiated.class::isInstance))
				.findAny()
				.isEmpty();

<<<<<<< HEAD
		return Result.from(result);
=======
		return ResultEvent.of(result);
>>>>>>> 6cc4c84 (prepare merge)
	}

	/**
	 * Handles the event by doing a simple interpretation.
	 * 
	 * @return the set of events resulting from the interpretation.
	 */
	@Subscribe
<<<<<<< HEAD
	public Result onWakeUpUserEvent(final UserWokeUp evt) {
=======
	public ResultEvent<DESEvent> onWakeUpUserEvent(final UserWokeUp evt) {
>>>>>>> 6cc4c84 (prepare merge)
		this.LOGGER.info("User woke up: " + evt.getEntity().getUser());
		return this.interpretNextAction(evt.getEntity());
	}

	/**
	 * Handles the event by doing a simple interpretation.
	 * 
	 * @return the set of events resulting from the interpretation.
	 */
	@Subscribe
<<<<<<< HEAD
	public Result onUserRequestFinished(final UserRequestFinished evt) {
=======
	public ResultEvent<DESEvent> onUserRequestFinished(final UserRequestFinished evt) {
>>>>>>> 6cc4c84 (prepare merge)
		return this.interpretNextAction(evt.getUserContext());
	}

	/**
	 * Handles the event of a user finished interpretation.
	 * <p>
	 * If the corresponding context holds a loop context, then this will be handled
	 * as having finished a inner scenario behavior. Depending on the current loop
	 * count, this will interpret again the start action of the loop's scenario
	 * behavior, or simply interpreting the next event after the loop action.
	 * <p>
	 * If, however, there is no loop context, then the whole scenario has been
	 * finished. It will remove the stack frame from the user, and, depending on the
	 * current usage simulation count, rerun the simulation by interpreting the
	 * first action again. If the maximum rerun count has been reached, then an
	 * empty set will be returned.
	 */
	@Subscribe
<<<<<<< HEAD
	public Result onUserFinished(final UserFinished evt) {
=======
	public ResultEvent<DESEvent> onUserFinished(final UserFinished evt) {
>>>>>>> 6cc4c84 (prepare merge)
		this.LOGGER.info("User finished: " + evt.getEntity());

		final Set<DESEvent> resultSet = new HashSet<>();
		final UserInterpretationContext context = evt.getEntity();

		if (context.getParentContext().isPresent()) {
			/* We are inside another behavior, such as loop or branch */
			final UsageScenarioBehaviorContext scenarioBehaviorContext = context.getBehaviorContext();
			final UserInterpretationContext newContext;

			if (scenarioBehaviorContext.mustRepeatScenario()) {
				newContext = context.update()
						.withCurrentAction(scenarioBehaviorContext.startScenario())
						.build();
			} else {
				newContext = context.getParentContext().get()
						.updateAction(scenarioBehaviorContext.getNextAction().get());
			}

			final UsageScenarioInterpreter interpreter = new UsageScenarioInterpreter(newContext);
			resultSet.addAll(interpreter.doSwitch(newContext.getCurrentAction()));
		} else {
			this.finishUserInterpretation(resultSet, context);
		}

<<<<<<< HEAD
		return Result.from(resultSet);
=======
		return ResultEvent.of(resultSet);
>>>>>>> 6cc4c84 (prepare merge)
	}

	/**
	 * Helper method which lets the user rerun the simulation again as long as the
	 * simulation hasn't been interrupted yet.
	 * 
	 * @param resultSet The set of events that should be published.
	 * @param context   The user's context.
	 */
	private void finishUserInterpretation(final Set<DESEvent> resultSet, final UserInterpretationContext context) {
		context.getUser().getStack().removeStackFrame();
//		this.startUsageSimulation(resultSet);
		/* 
		 * Error in Semantics before with the above statement:
		 *  - For OpenWorkloads, another user will be spawned which is already done by the InterArrivalUserInitiated event. 
		 *  - For ClosedWorkloads, old users will be replaced with new users, which is not exactly "re-entering" and it is
		 *    harder to let them only spawn after a ThinkTime.
		 * Instead, only for ClosedWorkloadUsers, let them re-enter the system after the ThinkTime.
		 * For OpenWorkloadUsers, this is already handled in the other event.
		 */
		if (context instanceof ClosedWorkloadUserInterpretationContext) {
			final ClosedWorkloadUserInterpretationContext closedContext = (ClosedWorkloadUserInterpretationContext) context;
			resultSet.add(new ClosedWorkloadUserInitiated(context, closedContext.getThinkTime()));
		}
		resultSet.add(new UsageScenarioFinished(context, 0));
	}

	/**
	 * Restarts the interpretation of a Usage Scenario by updating the
	 * {@link RootScenarioContext} and interpreting the first start action.
	 */
	@Subscribe
<<<<<<< HEAD
	public Result onClosedWorkloadUserInitiated(
=======
	public ResultEvent<DESEvent> onClosedWorkloadUserInitiated(
>>>>>>> 6cc4c84 (prepare merge)
			final ClosedWorkloadUserInitiated closedWorkloadUserInitiated) {
		final RootScenarioContext updatedRootScenarioContext = new RootScenarioContext(
				closedWorkloadUserInitiated.getEntity().getBehaviorContext().getScenarioBehavior());
		final UsageScenarioInterpreter usageScenarioInterpreter = new UsageScenarioInterpreter(
				closedWorkloadUserInitiated.getEntity());
<<<<<<< HEAD
		return Result.from(usageScenarioInterpreter.doSwitch(updatedRootScenarioContext.startScenario()));
	}

	@Subscribe
	public Result onInnerScenarioBehaviorInitiated(
=======
		return ResultEvent.of(usageScenarioInterpreter.doSwitch(updatedRootScenarioContext.startScenario()));
	}

	@Subscribe
	public ResultEvent<DESEvent> onInnerScenarioBehaviorInitiated(
>>>>>>> 6cc4c84 (prepare merge)
			final InnerScenarioBehaviorInitiated innerScenarioBehaviorInitiated) {
		final UserInterpretationContext userInterpretationContext = innerScenarioBehaviorInitiated.getEntity();
		final UsageScenarioInterpreter usageScenarioInterpreter = new UsageScenarioInterpreter(
				userInterpretationContext);
		final Set<DESEvent> events = usageScenarioInterpreter.doSwitch(userInterpretationContext.getCurrentAction());
<<<<<<< HEAD
		return Result.from(events);
=======
		return ResultEvent.of(events);
>>>>>>> 6cc4c84 (prepare merge)
	}

	/**
	 * Helper method for simply interpreting the next action.
	 * 
	 * Some events do not need extra work, but just pass on the next action to the
	 * interpreter. This method will simply interpret the next action and return a
	 * set of events resulting from that interpretation.
	 * 
	 * @param context The context holding for the interpreter.
	 * @return Set of events resulting from the interpretation.
	 */
<<<<<<< HEAD
	private Result interpretNextAction(final UserInterpretationContext context) {
		final UsageScenarioInterpreter interpreter = new UsageScenarioInterpreter(context);
		return Result.from(interpreter.doSwitch(context.getCurrentAction()));
=======
	private ResultEvent<DESEvent> interpretNextAction(final UserInterpretationContext context) {
		final UsageScenarioInterpreter interpreter = new UsageScenarioInterpreter(context);
		return ResultEvent.of(interpreter.doSwitch(context.getCurrentAction()));
>>>>>>> 6cc4c84 (prepare merge)
	}
}
