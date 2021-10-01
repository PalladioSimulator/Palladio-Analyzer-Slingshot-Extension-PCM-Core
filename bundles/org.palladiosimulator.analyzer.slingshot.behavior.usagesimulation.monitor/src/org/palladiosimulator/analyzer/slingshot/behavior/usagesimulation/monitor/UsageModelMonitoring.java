package org.palladiosimulator.analyzer.slingshot.behavior.usagesimulation.monitor;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.eclipse.emf.ecore.EObject;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UsageModelPassedElement;
import org.palladiosimulator.analyzer.slingshot.monitor.data.CalculatorRegistered;
import org.palladiosimulator.analyzer.slingshot.monitor.data.MonitorModelVisited;
import org.palladiosimulator.analyzer.slingshot.monitor.data.ProbeTaken;
import org.palladiosimulator.analyzer.slingshot.monitor.data.ProbeTakenEntity;
import org.palladiosimulator.analyzer.slingshot.monitor.probes.EventCurrentSimulationTimeProbe;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.SimulationBehaviorExtension;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.EventCardinality;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.OnEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.Reified;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.results.ResultEvent;
import org.palladiosimulator.commons.emfutils.EMFLoadHelper;
import org.palladiosimulator.edp2.models.measuringpoint.MeasuringPoint;
import org.palladiosimulator.metricspec.MetricDescription;
import org.palladiosimulator.metricspec.constants.MetricDescriptionConstants;
import org.palladiosimulator.monitorrepository.MeasurementSpecification;
import org.palladiosimulator.monitorrepository.MonitorRepository;
import org.palladiosimulator.pcm.usagemodel.Start;
import org.palladiosimulator.pcm.usagemodel.Stop;
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
@OnEvent(when = UsageModelPassedElement.class, whenReified = Start.class, then = ProbeTaken.class, cardinality = EventCardinality.SINGLE)
@OnEvent(when = UsageModelPassedElement.class, whenReified = Stop.class, then = ProbeTaken.class, cardinality = EventCardinality.SINGLE)
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
					getTuple(measurementSpecification.getMetricDescription()), measuringPoint,
					DefaultCalculatorProbeSets.createStartStopProbeConfiguration(userProbes.userStartedProbe,
							userProbes.userFinishedProbe));
			return ResultEvent.of(new CalculatorRegistered(calculator));
		} else {
			return ResultEvent.empty();
		}
	}

	@Subscribe
	public ResultEvent<ProbeTaken> onUsageScenarioStarted(
			@Reified(Start.class) final UsageModelPassedElement<Start> userStarted) {
		final UserProbes userProbes = this.userProbesMap.get(userStarted.getContext().getScenario().getId());
		if (userProbes != null) {
			userProbes.userStartedProbe.takeMeasurement(userStarted);
			return ResultEvent
					.of(new ProbeTaken(ProbeTakenEntity.builder().withProbe(userProbes.userStartedProbe).build()));
		} else {
			return ResultEvent.empty();
		}
	}

	@Subscribe
	public ResultEvent<ProbeTaken> onUsageScenarioFinished(
			@Reified(Stop.class) final UsageModelPassedElement<Stop> userStopped) {
		final UserProbes userProbes = this.userProbesMap.get(userStopped.getContext().getScenario().getId());
		if (userProbes != null) {
			userProbes.userFinishedProbe.takeMeasurement(userStopped);
			return ResultEvent
					.of(new ProbeTaken(ProbeTakenEntity.builder().withProbe(userProbes.userFinishedProbe).build()));
		} else {
			return ResultEvent.empty();
		}
	}

	private static MetricDescription getTuple(final MetricDescription metricDescription) {
		if (MetricDescriptionConstants.RESPONSE_TIME_METRIC.getId().equals(metricDescription.getId())) {
			return MetricDescriptionConstants.RESPONSE_TIME_METRIC_TUPLE;
		}
		return MetricDescriptionConstants.RESPONSE_TIME_METRIC_TUPLE;
	}

	private static final class UserProbes {
		private final EventCurrentSimulationTimeProbe userStartedProbe = new EventCurrentSimulationTimeProbe(
				UsageModelPassedElement.class, this::passedElement);
		private final EventCurrentSimulationTimeProbe userFinishedProbe = new EventCurrentSimulationTimeProbe(
				UsageModelPassedElement.class, this::passedElement);

		private RequestContext passedElement(final DESEvent desEvent) {
			if (desEvent instanceof UsageModelPassedElement<?>) {
				final UsageModelPassedElement<?> el = (UsageModelPassedElement<?>) desEvent;
				return new RequestContext(el.getContext().getUser().getId());
			}
			return RequestContext.EMPTY_REQUEST_CONTEXT;
		}
	}
}
