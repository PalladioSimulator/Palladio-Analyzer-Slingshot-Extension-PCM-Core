package org.palladiosimulator.analyzer.slingshot.behavior.usagesimulation.monitor;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.eclipse.emf.ecore.EObject;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UserFinished;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UserStarted;
import org.palladiosimulator.analyzer.slingshot.monitor.data.CalculatorRegistered;
import org.palladiosimulator.analyzer.slingshot.monitor.data.MonitorModelVisited;
import org.palladiosimulator.analyzer.slingshot.monitor.data.ProbeTaken;
import org.palladiosimulator.analyzer.slingshot.monitor.data.ProbeTakenEntity;
import org.palladiosimulator.analyzer.slingshot.monitor.probes.EventCurrentSimulationTimeProbe;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.SimulationBehaviorExtension;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.EventCardinality;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.OnEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.Reified;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.results.ResultEvent;
import org.palladiosimulator.commons.emfutils.EMFLoadHelper;
import org.palladiosimulator.edp2.models.measuringpoint.MeasuringPoint;
import org.palladiosimulator.monitorrepository.MeasurementSpecification;
import org.palladiosimulator.monitorrepository.MonitorRepository;
import org.palladiosimulator.pcm.usagemodel.UsageScenario;
import org.palladiosimulator.probeframework.calculator.Calculator;
import org.palladiosimulator.probeframework.calculator.DefaultCalculatorProbeSets;
import org.palladiosimulator.probeframework.calculator.IGenericCalculatorFactory;
import org.palladiosimulator.probeframework.measurement.RequestContext;

import com.google.common.eventbus.Subscribe;

/**
 * A usage model monitoring monitors each usage scenario in a usage model. For
 * that, every time a usage scenario has been
 * 
 * @author Julijan Katic
 *
 */
@OnEvent(when = MonitorModelVisited.class, whenReified = MeasurementSpecification.class, then = CalculatorRegistered.class, cardinality = EventCardinality.SINGLE)
@OnEvent(when = UserStarted.class, then = ProbeTaken.class, cardinality = EventCardinality.SINGLE)
@OnEvent(when = UserFinished.class, then = ProbeTaken.class, cardinality = EventCardinality.SINGLE)
public class UsageModelMonitoring implements SimulationBehaviorExtension {

	private final IGenericCalculatorFactory calculatorFactory;
	private final Map<String, UserProbes> userProbesMap = new HashMap<>();

	@Inject
	public UsageModelMonitoring(
			final IGenericCalculatorFactory calculatorFactory,
			final MonitorRepository monitorRepository) {
		this.calculatorFactory = calculatorFactory;
	}

	@Subscribe
	public ResultEvent<CalculatorRegistered> onMeasurementSpecification(
			@Reified(MeasurementSpecification.class) final MonitorModelVisited<MeasurementSpecification> measurementSpecificationVisited) {
		final MeasurementSpecification measurementSpecification = measurementSpecificationVisited.getModelElement();
		final MeasuringPoint measuringPoint = measurementSpecification.getMonitor().getMeasuringPoint();
		final EObject eObject = EMFLoadHelper.loadAndResolveEObject(measuringPoint.getResourceURIRepresentation());
		if (eObject instanceof UsageScenario) {
			final UsageScenario scenario = (UsageScenario) eObject;
			final UserProbes userProbes = new UserProbes();
			this.userProbesMap.put(scenario.getId(), userProbes);
			final Calculator calculator = this.calculatorFactory.buildCalculator(
					measurementSpecification.getMetricDescription(), measuringPoint,
					DefaultCalculatorProbeSets.createStartStopProbeConfiguration(userProbes.userStartedProbe,
							userProbes.userFinishedProbe));
			return ResultEvent.of(new CalculatorRegistered(calculator));
		} else {
			return ResultEvent.empty();
		}
	}

	@Subscribe
	public ResultEvent<ProbeTaken> onUserStarted(final UserStarted userStarted) {
		final UserProbes userProbes = this.userProbesMap.get(userStarted.getEntity().getScenario().getId());
		if (userProbes != null) {
			userProbes.userStartedProbe.takeMeasurement(userStarted);
			return ResultEvent
					.of(new ProbeTaken(ProbeTakenEntity.builder().withProbe(userProbes.userStartedProbe).build()));
		} else {
			return ResultEvent.empty();
		}
	}

	@Subscribe
	public ResultEvent<ProbeTaken> onUserFinished(final UserFinished userFinished) {
		final UserProbes userProbes = this.userProbesMap.get(userFinished.getEntity().getScenario().getId());
		if (userProbes != null) {
			userProbes.userFinishedProbe.takeMeasurement(userFinished);
			return ResultEvent
					.of(new ProbeTaken(ProbeTakenEntity.builder().withProbe(userProbes.userFinishedProbe).build()));
		} else {
			return ResultEvent.empty();
		}
	}

	private static final class UserProbes {
		private final EventCurrentSimulationTimeProbe<UserStarted> userStartedProbe = new EventCurrentSimulationTimeProbe<>(
				UserStarted.class, userStarted -> new RequestContext(userStarted.getEntity().getUser().getId()));
		private final EventCurrentSimulationTimeProbe<UserFinished> userFinishedProbe = new EventCurrentSimulationTimeProbe<>(
				UserFinished.class, userFinished -> new RequestContext(userFinished.getEntity().getUser().getId()));
	}
}
