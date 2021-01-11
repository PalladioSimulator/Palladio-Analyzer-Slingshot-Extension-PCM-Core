package org.palladiosimulator.analyzer.slingshot.behavior.usagesimulation;

import static org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.EventCardinality.MANY;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.UserEntryRequest;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.UserEntryRequested;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.UserInterpretationContext;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.ClosedWorkloadUserInterpretationContext;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.InterArrivalTime;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.OpenWorkloadUserInterpretationContext;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.ThinkTime;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.UsageInterpretationContext;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.UsageScenarioInterpretationContext;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.User;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.UserLoopContextHolder;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.InterArrivalUserInitiated;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UsageInterpretationEvent;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UserFinished;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UserLoopInitiated;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UserRequestFinished;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UserRequestInitiated;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UserStarted;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UserWokeUp;
import org.palladiosimulator.analyzer.slingshot.behavior.usagesimulation.interpreters.UsageScenarioInterpreter;
import org.palladiosimulator.analyzer.slingshot.behavior.usagesimulation.repositories.UsageModelRepository;
import org.palladiosimulator.analyzer.slingshot.common.utils.SimulatedStackHelper;
import org.palladiosimulator.analyzer.slingshot.simulation.core.events.SimulationStarted;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.SimulationBehaviorExtension;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.OnEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.results.ResultEvent;
import org.palladiosimulator.pcm.core.PCMRandomVariable;
import org.palladiosimulator.pcm.parameter.VariableUsage;
import org.palladiosimulator.pcm.usagemodel.AbstractUserAction;
import org.palladiosimulator.pcm.usagemodel.ClosedWorkload;
import org.palladiosimulator.pcm.usagemodel.OpenWorkload;
import org.palladiosimulator.pcm.usagemodel.UsageModel;
import org.palladiosimulator.pcm.usagemodel.UsageScenario;

import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

import de.uka.ipd.sdq.probfunction.math.IProbabilityFunctionFactory;
import de.uka.ipd.sdq.probfunction.math.impl.ProbabilityFunctionFactoryImpl;
import de.uka.ipd.sdq.simucomframework.variables.StackContext;
import de.uka.ipd.sdq.simucomframework.variables.cache.StoExCache;

/**
 * This behavior handles the events for the usage simulation.
 * 
 * It interprets the usage model by listening on the
 * {@link UsageInterpretationEvent}s. See the method documentation for further
 * information about the event handling.
 * 
 * @author Julijan Katic
 */
@OnEvent(when = SimulationStarted.class, then = UsageInterpretationEvent.class, cardinality = MANY)
@OnEvent(when = UserStarted.class, then = UsageInterpretationEvent.class, cardinality = MANY)
@OnEvent(when = UserFinished.class, then = UsageInterpretationEvent.class, cardinality = MANY)
@OnEvent(when = UserWokeUp.class, then = DESEvent.class, cardinality = MANY)
@OnEvent(when = UserRequestFinished.class, then = DESEvent.class, cardinality = MANY)
@OnEvent(when = UserRequestInitiated.class, then = { UserEntryRequested.class,
        UsageInterpretationEvent.class }, cardinality = MANY)
@OnEvent(when = UserLoopInitiated.class, then = UsageInterpretationEvent.class, cardinality = MANY)
public class UsageSimulationBehavior implements SimulationBehaviorExtension {

	private final Logger LOGGER = Logger.getLogger(UsageSimulationBehavior.class);

	private UsageInterpretationContext usageInterpretationContext;

	/** The repository for access into the usage model. */
	private final UsageModelRepository usageModelRepository;

	/** The model to interpret. */
	private final UsageModel usageModel;

	// TODO: This shouldn't be used.
	/** The maximum amount of runs to be done. */
	private final int maximalUsageRuns = 1;

	@Inject
	public UsageSimulationBehavior(final UsageModel usageModel, final UsageModelRepository repository) {
		this.usageModel = usageModel;
		this.usageModelRepository = repository;
	}

	@Override
	public void init() {
		this.usageModelRepository.load(usageModel);
		usageInterpretationContext = UsageInterpretationContext.builder()
				.withUsageScenariosContexts(
						this.usageModelRepository.findAllUsageScenarios().stream()
						.map(scenario -> UsageScenarioInterpretationContext.builder().withScenario(scenario).build())
						.collect(Collectors.collectingAndThen(Collectors.toList(), ImmutableList::copyOf))
				)
				.build();
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
		
		for (UsageScenarioInterpretationContext usageScenarioContext : usageInterpretationContext.getUsageScenarioContexts()) {
			final UsageScenario usageScenario = usageScenarioContext.getScenario();
			final AbstractUserAction firstAction = usageModelRepository.findFirstActionOf(usageScenario);
			
			if (usageScenarioContext.isClosedWorkload()) {
				interpreteClosedWorkload(returnedEvents, usageScenario, firstAction);
			} else {
				interpreteOpenWorkload(returnedEvents, usageScenario, firstAction);
			}
		}

		return ResultEvent.ofAll(returnedEvents);
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
		
		/* Because this is the first action, this should only contain the UserStarted and InterArrivalUserInitiated events. */
		assert events.size() == 2;
		assert events.stream().anyMatch(event -> event instanceof UserStarted);
		assert events.stream().anyMatch(event -> event instanceof InterArrivalUserInitiated);
		
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

		final UserInterpretationContext context = evt.getEntity();
		final UserLoopContextHolder loopContext = context.getUserLoopContextHolder();

		if (loopContext != null) {
			if (!loopContext.hasLoopFinished()) {
				final UsageScenarioInterpreter interpreter = new UsageScenarioInterpreter(context.incrementLoopProgression());

				/* We need to loop again, hence start from the inside loop's start event */
				return ResultEvent.of(interpreter.doSwitch(loopContext.getLoopStartAction()));
			} else {
				/* Loop has finished. Start with the event after the loop */
				assert context.getParentContext() != null;
				final UsageScenarioInterpreter interpreter = new UsageScenarioInterpreter(context.getParentContext());
				return ResultEvent.of(interpreter.doSwitch(loopContext.getAfterLoopAction()));
			}
		}

		context.getUser().getStack().removeStackFrame();

		/* Rerun the usage scenario after a certain think time. */
		if (context.getCurrentUsageRun() < this.maximalUsageRuns) {
			final UserInterpretationContext incrementedContext = context.incrementUsageRun();
			final UsageScenarioInterpreter interpreter = new UsageScenarioInterpreter(incrementedContext);

			final Set<DESEvent> events = interpreter.caseScenarioBehaviour(
			        context.getScenario().getScenarioBehaviour_UsageScenario());
			return ResultEvent.ofAll(events);
		}

		return ResultEvent.empty();
	}

	// TODO: Remove this method and let the SystemSimulation listen on it
	/**
	 * Handles the UserRequestInitiated event by passing a request event to a system
	 * simulation. This will result in a {@link UserEntryRequested} event, as well
	 * as by interpreting the next event after the EntryLevelSystemCall action that
	 * will be posted afterwards.
	 * 
	 * @return
	 */
	@Subscribe
	public ResultEvent<DESEvent> onUserRequestInitiated(final UserRequestInitiated userRequestInit) {

		final User user = userRequestInit.getEntity().getUser();
		final EList<VariableUsage> variableUsages = userRequestInit.getEntity().getVariableUsages();

		/* An entry to the system is requested. This leads to the creation of a stack frame */
		SimulatedStackHelper.createAndPushNewStackFrame(user.getStack(), variableUsages);

		final UserEntryRequest request = new UserEntryRequest(user,
		        userRequestInit.getEntity().getOperationProvidedRole(),
		        userRequestInit.getEntity().getOperationSignature(),
		        variableUsages);

		final UsageScenarioInterpreter interpreter = new UsageScenarioInterpreter(
		        userRequestInit.getUserContext());

		/* Return the EntryRequestEvent and also the event that happens after the request. */
		return ResultEvent.<DESEvent>of(new UserEntryRequested(request, 0))
		        .and(interpreter.doSwitch(userRequestInit.getUserContext().getCurrentAction()));
	}

	/**
	 * Handles the UserLoopInitiated event by interpreting the first action of the
	 * inner scenario behavior.
	 */
	@Subscribe
	public ResultEvent<DESEvent> onUserLoopInitiated(final UserLoopInitiated userLoopInitiated) {
		final UserInterpretationContext userInterpretationContext = userLoopInitiated.getEntity();
		final UsageScenarioInterpreter usageScenarioInterpreter = new UsageScenarioInterpreter(userInterpretationContext);
		final Set<DESEvent> appearedEvents = usageScenarioInterpreter.doSwitch(userInterpretationContext.getCurrentAction());
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
