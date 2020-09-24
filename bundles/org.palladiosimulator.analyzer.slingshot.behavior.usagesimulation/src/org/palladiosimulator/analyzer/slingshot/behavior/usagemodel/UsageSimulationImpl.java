package org.palladiosimulator.analyzer.slingshot.behavior.usagemodel;

import java.util.Set;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.User;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UserFinished;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UserRequestFinished;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UserRequestInitiated;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UserSlept;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UserStarted;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UserWokeUp;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.interpreters.UsageScenarioInterpreter;
import org.palladiosimulator.analyzer.slingshot.repositories.UsageModelRepository;
import org.palladiosimulator.analyzer.slingshot.simulation.api.SimulationModel;
import org.palladiosimulator.analyzer.slingshot.simulation.core.events.SimulationStarted;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.behavioural.SimulationBehaviourExtension;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.behavioural.annotations.EventCardinality;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.behavioural.annotations.OnEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.behavioural.results.ResultEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.behavioural.results.ResultEventBuilder;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.pcm.usagemodel.AbstractUserAction;
import org.palladiosimulator.pcm.usagemodel.ClosedWorkload;
import org.palladiosimulator.pcm.usagemodel.UsageModel;
import org.palladiosimulator.pcm.usagemodel.UsageScenario;

import com.google.common.eventbus.Subscribe;

@OnEvent(when = SimulationStarted.class, then = { UserRequestInitiated.class, UserFinished.class, UserStarted.class,
		UserSlept.class, UserWokeUp.class }, cardinality = EventCardinality.MANY)
//@OnEvent(eventType = UserStarted.class, outputEventType = UserFinished.class, cardinality = EventCardinality.SINGLE)
@OnEvent(when = UserFinished.class, then = DESEvent.class, cardinality = EventCardinality.SINGLE)
@OnEvent(when = UserWokeUp.class, then = DESEvent.class, cardinality = EventCardinality.SINGLE)
public class UsageSimulationImpl implements SimulationBehaviourExtension {

	private final Logger LOGGER = Logger.getLogger(UsageSimulationImpl.class);

	private UsageInterpretationContext usageInterpretationContext;
	private UsageModelRepository usageModelRepository;
	private UsageModel usageModel;

	public UsageSimulationImpl() {

	}

	@Inject
	public UsageSimulationImpl(final UsageModelRepository usageModelRepository,
			final SimulatedUserProvider simulatedUserProvider,
			final UsageModel usageModel) {
		this.usageModelRepository = usageModelRepository;
	}

	@Override
	public void init(final SimulationModel model) {
		loadModel(usageModel);
		usageInterpretationContext = new UsageInterpretationContext(
				usageModelRepository.findAllUsageScenarios().get(0));
		LOGGER.info("Usage Simulation Extension Started");
	}

	// @OnEvent(output=UserStarted -> checker its fine ... -> check them all)
	// @OnEvnet(output=DESEvent -> reject immediatly -> fine
	// FIXME: can these checks be done all at compile-time, so why we need runtime
	// checks
	// FIXME: how can it occur that an extension sends an event which is not
	// specified
	// FIXME: when and how could it happen that an extension sends an event which
	// has no specification for it.
	// FIXME: explore runtime checks and in combination with rule checks.
	// TODO: Also when enforcing the contract rather then observing the result
	// object directly we could easily check whether
	// the method that was invoked has the generic type of UserStarted.
	@Subscribe
	public ResultEvent<DESEvent> onSimulationStart(final SimulationStarted evt) {
		final ResultEventBuilder<DESEvent> builder = ResultEvent.createResult();

		// 1. determine the number of users by traversing the scenario in the model.
		// - UsageModelInterpreter . continueInterpretation() returns a
		// UsageInterpretationContext which has the type of workload: closed the number
		// of users: 7
		// - and this can be stored as a member of this field and initialized in init.

		// 2. for the number of users continue UsageScenarioInterpretation
		final UsageScenario usageScenario = usageInterpretationContext.getUsageScenario();
		final AbstractUserAction firstAction = usageModelRepository.findFirstActionOf(usageScenario);

		if (usageInterpretationContext.isClosedWorkload()) {
			final ClosedWorkload workloadSpec = (ClosedWorkload) usageInterpretationContext.getWorkload();

			for (int i = 0; i < workloadSpec.getPopulation(); i++) {
				final UsageScenarioInterpreter<Object> interpreter = new UsageScenarioInterpreter<>(new User(),
						new UserInterpretationContext(usageScenario, firstAction));
				interpreter.continueInterpretation();
				builder.addAll(interpreter.getSideEffectEvents());
			}

		}

		return builder.build();
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

	private void loadModel(final UsageModel usageModel) {
		usageModelRepository.load(usageModel);
		LOGGER.info("UsageSimulation: usage model was loaded");
	}
}
