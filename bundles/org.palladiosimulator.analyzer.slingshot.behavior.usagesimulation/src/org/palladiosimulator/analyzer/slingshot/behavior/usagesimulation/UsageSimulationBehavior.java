package org.palladiosimulator.analyzer.slingshot.behavior.usagesimulation;

import static org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.EventCardinality.MANY;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.UserEntryRequest;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.UserEntryRequested;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.InterArrivalTime;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.ThinkTime;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.User;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.interpretationcontext.ClosedWorkloadUserInterpretationContext;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.interpretationcontext.OpenWorkloadUserInterpretationContext;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.interpretationcontext.UsageInterpretationContext;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.interpretationcontext.UsageScenarioInterpretationContext;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.interpretationcontext.UserBranchInterpretationContext;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.interpretationcontext.UserInterpretationContext;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.interpretationcontext.UserLoopInterpretationContext;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.InterArrivalUserInitiated;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UserBranchInitiated;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UserFinished;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UserLoopInitiated;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UserRequestFinished;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UserSlept;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UserStarted;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UserWokeUp;
import org.palladiosimulator.analyzer.slingshot.behavior.usagesimulation.interpreters.UsageScenarioInterpreter;
import org.palladiosimulator.analyzer.slingshot.behavior.usagesimulation.repositories.UsageModelRepository;
import org.palladiosimulator.analyzer.slingshot.common.utils.Postconditions;
import org.palladiosimulator.analyzer.slingshot.simulation.core.events.SimulationStarted;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.SimulationBehaviorExtension;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.OnEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.results.ResultEvent;
import org.palladiosimulator.pcm.core.PCMRandomVariable;
import org.palladiosimulator.pcm.usagemodel.AbstractUserAction;
import org.palladiosimulator.pcm.usagemodel.ClosedWorkload;
import org.palladiosimulator.pcm.usagemodel.OpenWorkload;
import org.palladiosimulator.pcm.usagemodel.UsageModel;
import org.palladiosimulator.pcm.usagemodel.UsageScenario;

import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;


/**
 * This behavior handles the events for the usage simulation.
 * 
 * It interprets the usage model by listening on the
 * {@link UsageInterpretationEvent}s. See the method documentation for further
 * information about the event handling.
 * 
 * @author Julijan Katic
 */
@OnEvent(when = SimulationStarted.class, then = {UserStarted.class, InterArrivalUserInitiated.class}, cardinality = MANY)
@OnEvent(when = UserStarted.class, then = {UserFinished.class, UserEntryRequested.class, UserSlept.class, UserWokeUp.class, UserLoopInitiated.class, UserBranchInitiated.class}, cardinality = MANY)
@OnEvent(when = UserFinished.class, then = {UserStarted.class, InterArrivalUserInitiated.class}, cardinality = MANY)
@OnEvent(when = UserWokeUp.class, then = {UserFinished.class, UserEntryRequested.class, UserSlept.class, UserWokeUp.class, UserLoopInitiated.class, UserBranchInitiated.class}, cardinality = MANY)
@OnEvent(when = UserRequestFinished.class, then = {UserFinished.class, UserEntryRequested.class, UserSlept.class, UserWokeUp.class, UserLoopInitiated.class, UserBranchInitiated.class}, cardinality = MANY)
@OnEvent(when = UserLoopInitiated.class, then = {UserStarted.class}, cardinality = MANY)
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
	}

	@Override
	public void init() {
		this.usageModelRepository.load(usageModel);
		usageInterpretationContext = UsageInterpretationContext.builder()
				.withUsageScenariosContexts(this.collectAllUsageScenarios())
				.build();
	}
	
	/**
	 * Helper method in order to collect all available usage scenarios and map them into the
	 * {@link UsageScenarioInterpretationContext}.
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
	public ResultEvent<DESEvent> onSimulationStart(final SimulationStarted evt) {
		final Set<DESEvent> returnedEvents = new HashSet<>();
		
		this.startUsageSimulation(returnedEvents);
		
		/* Because this is the first action, this should only contain the UserStarted and InterArrivalUserInitiated events. */
		assert Postconditions.checkResultEventTypesAndSize(returnedEvents, List.of(UserStarted.class, InterArrivalUserInitiated.class), 2);
		
		return ResultEvent.ofAll(returnedEvents);
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
		
		for (UsageScenarioInterpretationContext usageScenarioContext : usageInterpretationContext.getUsageScenarioContexts()) {
			final UsageScenario usageScenario = usageScenarioContext.getScenario();
			final AbstractUserAction firstAction = usageModelRepository.findFirstActionOf(usageScenario);
			
			if (usageScenarioContext.isClosedWorkload()) {
				interpreteClosedWorkload(returnedEvents, usageScenario, firstAction);
			} else {
				interpreteOpenWorkload(returnedEvents, usageScenario, firstAction);
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
		final OpenWorkload workloadSpec = (OpenWorkload) usageScenario.getWorkload_UsageScenario();
		final PCMRandomVariable interArrivalRV = workloadSpec.getInterArrivalTime_OpenWorkload();

		final OpenWorkloadUserInterpretationContext openWorkloadUserInterpretationContext = OpenWorkloadUserInterpretationContext.builder()
					.withUser(new User())
					.withScenario(usageScenario)
					.withCurrentAction(firstAction)
					.withInterArrivalTime(new InterArrivalTime(interArrivalRV))
					.build();
		
		final UsageScenarioInterpreter interpreter = new UsageScenarioInterpreter(openWorkloadUserInterpretationContext);
		
		final Set<DESEvent> events = interpreter.doSwitch(firstAction);
		
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
		final ClosedWorkload workloadSpec = (ClosedWorkload) usageScenario.getWorkload_UsageScenario();

		for (int i = 0; i < workloadSpec.getPopulation(); i++) {
			final UserInterpretationContext interpretationContext = ClosedWorkloadUserInterpretationContext.builder()
			        .withThinkTime(new ThinkTime(workloadSpec.getThinkTime_ClosedWorkload()))
			        .withUser(new User())
			        .withCurrentAction(firstAction)
			        .build();

			final UsageScenarioInterpreter interpreter = new UsageScenarioInterpreter(interpretationContext);
			returnedEvents.addAll(interpreter.doSwitch(firstAction));
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
	public ResultEvent<DESEvent> onUserStarted(final UserStarted userStarted) {
		userStarted.getEntity().getUser().getStack().createAndPushNewStackFrame();
		final UsageScenarioInterpreter interpreter = new UsageScenarioInterpreter(
		        userStarted.getEntity());
		return ResultEvent.of(interpreter.doSwitch(userStarted.getEntity().getCurrentAction()));
	}
	
	/**
	 * Creates a new set of open workload users for each user scenario. Returns the events
	 * of 
	 * 
	 * @param interArrivalUserInitiated
	 * @return
	 */
	@Subscribe
	public ResultEvent<DESEvent> onInterArrivalUserInitiated(final InterArrivalUserInitiated interArrivalUserInitiated) {
		final Set<DESEvent> result = new HashSet<>();
		
		for (UsageScenarioInterpretationContext usageScenarioContext : usageInterpretationContext.getUsageScenarioContexts()) {
			this.interpreteOpenWorkload(result, usageScenarioContext.getScenario(), usageModelRepository.findFirstActionOf(usageScenarioContext.getScenario()));
		}
		
		return ResultEvent.of(result);
	}

	/**
	 * Handles the event by doing a simple interpretation.
	 * 
	 * @return the set of events resulting from the interpretation.
	 */
	@Subscribe
	public ResultEvent<DESEvent> onWakeUpUserEvent(final UserWokeUp evt) {
		return this.interpretNextAction(evt.getEntity());
	}

	/**
	 * Handles the event by doing a simple interpretation.
	 * 
	 * @return the set of events resulting from the interpretation.
	 */
	@Subscribe
	public ResultEvent<DESEvent> onUserRequestFinished(final UserRequestFinished evt) {
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
	public ResultEvent<DESEvent> onUserFinished(final UserFinished evt) {
		LOGGER.info("User finished: " + evt.getEntity());

		final Set<DESEvent> resultSet = new HashSet<>();
		final UserInterpretationContext context = evt.getEntity();
		
		if (context.getParentContext().isPresent()) {
			/* We are inside another behavior, such as loop or branch */
			checkLoopProgression(resultSet, context);
		} else {
			finishUserInterpretation(resultSet, context);
		}

		return ResultEvent.of(resultSet);
	}

	/**
	 * Helper method which lets the user rerun the simulation again as long as the simulation
	 * hasn't been interrupted yet.
	 * 
	 * @param resultSet The set of events that should be published.
	 * @param context The user's context.
	 */
	private void finishUserInterpretation(final Set<DESEvent> resultSet, final UserInterpretationContext context) {
		context.getUser().getStack().removeStackFrame();
		this.startUsageSimulation(resultSet);
	}

	/**
	 * Helper method to check whether the run is currently inside a loop and if it is the case, the
	 * loop counter is decreased and restarted if the loop counter is not already 0.
	 * 
	 * @param resultSet The set of events that should be published afterwards.
	 * @param context The context of the user.
	 */
	private void checkLoopProgression(final Set<DESEvent> resultSet, final UserInterpretationContext context) {
		assert resultSet != null;
		assert context != null && context.getParentContext().isPresent();
		
		if (context.getCurrentLoopInterpretationContext().isEmpty()) {
			return;
		}
		
		final UserLoopInterpretationContext userLoopInterpretationContext = context.getCurrentLoopInterpretationContext().get();
		
		final UsageScenarioInterpreter interpreter;
		
		/*
		 * If the loop has finished, interpret the next action coming after the loop action itself.
		 * Otherwise, interpret the first action within the loop scenario again.
		 */
		if (userLoopInterpretationContext.isLoopFinished()) {
			 interpreter = new UsageScenarioInterpreter(context.getParentContext().get());
			 final Optional<AbstractUserAction> afterLoopAction = userLoopInterpretationContext.getUserLoopScenarioBehavior().getNextAction();
			 if (afterLoopAction.isPresent()) {
				 resultSet.addAll(interpreter.doSwitch(afterLoopAction.get()));
			 } else {
				 LOGGER.info("There is no action after the loop");
			 }
		} else {
			final UserInterpretationContext newContext = context.update()
																.withUserLoopInterpretationContext(
																		userLoopInterpretationContext.progress()
																)
																.build();
			interpreter = new UsageScenarioInterpreter(newContext);
			resultSet.addAll(interpreter.doSwitch(userLoopInterpretationContext.getStartAction()));
		}
	}
	
	/*
	 * TODO: The following two operations are nearly the same. Consider using a single event for inner behaviors.
	 */
	/**
	 * Handles the UserLoopInitiated event by interpreting the first action of the
	 * inner scenario behavior.
	 */
	@Subscribe
	public ResultEvent<DESEvent> onUserLoopInitiated(final UserLoopInitiated userLoopInitiated) {
		final UserLoopInterpretationContext userLoopInterpretationContext = userLoopInitiated.getEntity();
		final UsageScenarioInterpreter usageScenarioInterpreter = new UsageScenarioInterpreter(userLoopInterpretationContext.getUserInterpretationContext());
		
		final Set<DESEvent> appearedEvents = usageScenarioInterpreter.doSwitch(userLoopInterpretationContext.getUserInterpretationContext().getCurrentAction());
		return ResultEvent.of(appearedEvents);
	}
	
	/**
	 * Handles the UserLoopInitiated event by interpreting the first action of the
	 * inner scenario behavior.
	 */
	@Subscribe
	public ResultEvent<DESEvent> onUserBranchInitiated(final UserBranchInitiated userLoopInitiated) {
		final UserBranchInterpretationContext userBranchInterpretationContext = userLoopInitiated.getEntity();
		final UsageScenarioInterpreter usageScenarioInterpreter = new UsageScenarioInterpreter(userBranchInterpretationContext.getUserInterpretationContext());
		
		final Set<DESEvent> appearedEvents = usageScenarioInterpreter.doSwitch(userBranchInterpretationContext.getUserInterpretationContext().getCurrentAction());
		return ResultEvent.of(appearedEvents);
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
	private ResultEvent<DESEvent> interpretNextAction(final UserInterpretationContext context) {
		final UsageScenarioInterpreter interpreter = new UsageScenarioInterpreter(context);
		return ResultEvent.of(interpreter.doSwitch(context.getCurrentAction()));
	}
}
