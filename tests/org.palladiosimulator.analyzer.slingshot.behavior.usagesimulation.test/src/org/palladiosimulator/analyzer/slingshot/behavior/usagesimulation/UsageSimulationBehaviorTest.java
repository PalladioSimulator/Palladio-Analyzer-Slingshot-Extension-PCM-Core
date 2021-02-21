package org.palladiosimulator.analyzer.slingshot.behavior.usagesimulation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import org.eclipse.emf.common.util.URI;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.User;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.interpretationcontext.ClosedWorkloadUserInterpretationContext;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.scenariobehavior.RootScenarioContext;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UserSlept;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UserStarted;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UserWokeUp;
import org.palladiosimulator.analyzer.slingshot.behavior.usagesimulation.repositories.UsageModelRepository;
import org.palladiosimulator.analyzer.slingshot.behavior.usagesimulation.repositories.UsageModelRepositoryImpl;
import org.palladiosimulator.analyzer.slingshot.common.serialization.load.PCMFileLoader;
import org.palladiosimulator.analyzer.slingshot.simulation.core.events.SimulationStarted;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.results.ResultEvent;
import org.palladiosimulator.pcm.usagemodel.AbstractUserAction;
import org.palladiosimulator.pcm.usagemodel.Delay;
import org.palladiosimulator.pcm.usagemodel.UsageModel;

import de.uka.ipd.sdq.probfunction.math.IProbabilityFunctionFactory;
import de.uka.ipd.sdq.probfunction.math.impl.ProbabilityFunctionFactoryImpl;
import de.uka.ipd.sdq.simucomframework.variables.cache.StoExCache;

/**
 * This tests the behavior by simulating events.
 * 
 * @author Julijan Katic
 *
 */
public class UsageSimulationBehaviorTest {

	private static final String MODEL_FOLDER = "/org.palladiosimulator.analyzer.slingshot.behavior.usagesimulation.test";
	private static final String USAGE_MODEL_PATH = MODEL_FOLDER + "/sampleUsageModel.usagemodel";
	private static final URI USAGE_MODEL_URI = URI.createPlatformPluginURI(USAGE_MODEL_PATH, true);

	private UsageSimulationBehavior usageSimulationBehavior;
	private final UsageModelRepository repository = new UsageModelRepositoryImpl();
	private final UsageModel usageModel = constructUsageModel();

	@BeforeAll
	public static void initializeCache() {
		final IProbabilityFunctionFactory probabilityFunctionFactory = ProbabilityFunctionFactoryImpl.getInstance();
		StoExCache.initialiseStoExCache(probabilityFunctionFactory);
	}

	@BeforeEach
	public void initializeSimulationBehavior() {
		this.usageSimulationBehavior = spy(new UsageSimulationBehavior(this.usageModel, this.repository));
		this.usageSimulationBehavior.init();
	}

	@Test
	public void testSimulationStarted() {
		final SimulationStarted simulationStarted = new SimulationStarted();

		final ResultEvent<DESEvent> resultEvent = this.usageSimulationBehavior.onSimulationStart(simulationStarted);

		assertFalse(resultEvent.isEmpty());
		assertTrue(resultEvent.getEventsForScheduling().stream().allMatch(UserStarted.class::isInstance));

		assertTrue(resultEvent.getEventsForScheduling().stream().map(UserStarted.class::cast)
				.map(UserStarted::getEntity).allMatch(ClosedWorkloadUserInterpretationContext.class::isInstance));
	}

	@Test
	public void testUserStarted() {
		final ClosedWorkloadUserInterpretationContext context = mock(ClosedWorkloadUserInterpretationContext.class);
		final RootScenarioContext rootContext = mock(RootScenarioContext.class);

		when(context.getBehaviorContext()).thenReturn(rootContext);
		when(context.getUser()).thenReturn(new User());
		when(context.updateAction(any(AbstractUserAction.class))).thenCallRealMethod();
		when(context.update()).thenCallRealMethod();

		final AbstractUserAction action = this.usageModel.getUsageScenario_UsageModel().get(0)
				.getScenarioBehaviour_UsageScenario().getActions_ScenarioBehaviour().get(0).getSuccessor();

		assert action instanceof Delay;

		when(context.getCurrentAction()).thenReturn(action);

		final UserStarted userStarted = new UserStarted(context);

		final ResultEvent<DESEvent> events = this.usageSimulationBehavior.onUserStarted(userStarted);

		/* According to the usage model, the next thing would be a delay action. */
		assertEquals(2, events.getEventsForScheduling().size());
		assertTrue(events.getEventsForScheduling().stream()
				.allMatch(event -> event instanceof UserSlept || event instanceof UserWokeUp));
		assertTrue(events.getEventsForScheduling().stream().filter(UserWokeUp.class::isInstance)
				.map(UserWokeUp.class::cast).allMatch(event -> event.getDelay() == 2));
	}

	/**
	 * Helper method that constructs a sample usage model.
	 * 
	 * @return
	 */
	private static UsageModel constructUsageModel() {
		final UsageModel usageModel = PCMFileLoader.load(USAGE_MODEL_URI);
		return usageModel;
	}
}
