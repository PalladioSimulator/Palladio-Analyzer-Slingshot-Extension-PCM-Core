package org.palladiosimulator.analyzer.slingshot.behavior.usagesimulation;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.UserEntryRequest;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.UserEntryRequested;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.UsageInterpretationContext;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.UserInterpretationContext;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.User;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UserFinished;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UserRequestFinished;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UserRequestInitiated;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UserSlept;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UserStarted;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UserWokeUp;
import org.palladiosimulator.analyzer.slingshot.behavior.usagesimulation.interpreters.UsageScenarioInterpreter;
import org.palladiosimulator.analyzer.slingshot.common.utils.SimulatedStackHelper;
import org.palladiosimulator.analyzer.slingshot.repositories.UsageModelRepository;
import org.palladiosimulator.analyzer.slingshot.simulation.core.events.SimulationStarted;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.SimulationBehaviorExtension;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.EventCardinality;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.OnEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.results.ResultEvent;
import org.palladiosimulator.pcm.parameter.VariableUsage;
import org.palladiosimulator.pcm.usagemodel.AbstractUserAction;
import org.palladiosimulator.pcm.usagemodel.ClosedWorkload;
import org.palladiosimulator.pcm.usagemodel.OpenWorkload;
import org.palladiosimulator.pcm.usagemodel.UsageModel;
import org.palladiosimulator.pcm.usagemodel.UsageScenario;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

@OnEvent(when = SimulationStarted.class, then = { UserRequestInitiated.class, UserFinished.class, UserStarted.class,
        UserSlept.class, UserWokeUp.class }, cardinality = EventCardinality.MANY)
//@OnEvent(eventType = UserStarted.class, outputEventType = UserFinished.class, cardinality = EventCardinality.SINGLE)
@OnEvent(when = UserFinished.class, then = DESEvent.class, cardinality = EventCardinality.MANY)
@OnEvent(when = UserWokeUp.class, then = DESEvent.class, cardinality = EventCardinality.MANY)
@OnEvent(when = UserRequestFinished.class, then = DESEvent.class, cardinality = EventCardinality.MANY)
@OnEvent(when = UserRequestInitiated.class, then = UserEntryRequested.class, cardinality = EventCardinality.SINGLE)
public class UsageSimulationBehavior implements SimulationBehaviorExtension {

	private final Logger LOGGER = Logger.getLogger(UsageSimulationBehavior.class);

	private UsageInterpretationContext usageInterpretationContext;
	private final UsageModelRepository usageModelRepository;
	private final UsageModel usageModel;

	@Inject
	public UsageSimulationBehavior(final UsageModel usageModel, final UsageModelRepository repository) {
		this.usageModel = usageModel;
		this.usageModelRepository = repository;
	}

	@Override
	public void init() {
		this.loadModel(usageModel);
		usageInterpretationContext = new UsageInterpretationContext(
		        usageModelRepository.findAllUsageScenarios().get(0));
		LOGGER.info("Usage Simulation Extension Started");
	}

	@Subscribe
	public ResultEvent<DESEvent> onSimulationStart(final SimulationStarted evt) {
		final Set<DESEvent> returnedEvents = new HashSet<>();

		final UsageScenario usageScenario = usageInterpretationContext.getUsageScenario();
		final AbstractUserAction firstAction = usageModelRepository.findFirstActionOf(usageScenario);

		if (usageInterpretationContext.isClosedWorkload()) {
			final ClosedWorkload workloadSpec = (ClosedWorkload) usageInterpretationContext.getWorkload();

			for (int i = 0; i < workloadSpec.getPopulation(); i++) {
				final UsageScenarioInterpreter<Object> interpreter = new UsageScenarioInterpreter<>(new User(),
				        new UserInterpretationContext(usageScenario, firstAction));
				interpreter.continueInterpretation();
				returnedEvents.addAll(interpreter.getSideEffectEvents());
			}

		} else if (usageInterpretationContext.isOpenWorkload()) {
			final OpenWorkload workloadSpec = (OpenWorkload) usageInterpretationContext.getWorkload();
			// TODO: Open Workload
		}

		return ResultEvent.ofAll(returnedEvents);
	}

	@Subscribe
	public ResultEvent<DESEvent> onWakeUpUserEvent(final UserWokeUp evt) {
		final UsageScenarioInterpreter<Object> interpreter = new UsageScenarioInterpreter<>(evt.getEntity(),
		        evt.getUserInterpretationContext());
		interpreter.continueInterpretation();
		final Set<DESEvent> events = interpreter.getSideEffectEvents();
		return ResultEvent.ofAll(events);
	}

	@Subscribe
	public ResultEvent<DESEvent> onFinishUserRequest(final UserRequestFinished evt) {
		final UsageScenarioInterpreter<Object> interpreter = new UsageScenarioInterpreter<>(evt.getEntity().getUser(),
		        evt.getUserContext());
		interpreter.continueInterpretation();
		final Set<DESEvent> events = interpreter.getSideEffectEvents();
		return ResultEvent.ofAll(events);
	}

	@Subscribe
	public ResultEvent<DESEvent> onUserFinished(final UserFinished evt) {
		final UsageScenarioInterpreter<Object> interpreter = new UsageScenarioInterpreter<Object>(evt.getEntity(),
		        evt.getUserInterpretationContext());
		interpreter.caseScenarioBehaviour(
		        evt.getUserInterpretationContext().getScenario().getScenarioBehaviour_UsageScenario());
		final Set<DESEvent> events = interpreter.getSideEffectEvents();
		return ResultEvent.ofAll(events);
	}

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

		return ResultEvent.of(new UserEntryRequested(request, 0));
	}

	private void loadModel(final UsageModel usageModel) {
		usageModelRepository.load(usageModel);
		LOGGER.info("UsageSimulation: usage model was loaded");
	}
}
