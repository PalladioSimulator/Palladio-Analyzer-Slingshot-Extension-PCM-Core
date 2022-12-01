package org.palladiosimulator.analyzer.slingshot.behavior.usagesimulation.monitor;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.eclipse.emf.ecore.EObject;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UsageModelPassedElement;
<<<<<<< HEAD
import org.palladiosimulator.analyzer.slingshot.common.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.core.extension.SimulationBehaviorExtension;
import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.Subscribe;
import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.eventcontract.EventCardinality;
import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.eventcontract.OnEvent;
import org.palladiosimulator.analyzer.slingshot.eventdriver.returntypes.Result;
import org.palladiosimulator.analyzer.slingshot.monitor.data.entities.ProbeTakenEntity;
import org.palladiosimulator.analyzer.slingshot.monitor.data.events.CalculatorRegistered;
import org.palladiosimulator.analyzer.slingshot.monitor.data.events.ProbeTaken;
import org.palladiosimulator.analyzer.slingshot.monitor.data.events.modelvisited.MeasurementSpecificationVisited;
import org.palladiosimulator.analyzer.slingshot.monitor.data.events.modelvisited.MonitorModelVisited;
import org.palladiosimulator.analyzer.slingshot.monitor.utils.probes.EventCurrentSimulationTimeProbe;
import org.palladiosimulator.commons.emfutils.EMFLoadHelper;
import org.palladiosimulator.metricspec.constants.MetricDescriptionConstants;
import org.palladiosimulator.monitorrepository.MeasurementSpecification;
=======
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
import org.palladiosimulator.edp2.util.MetricDescriptionUtility;
import org.palladiosimulator.metricspec.MetricDescription;
import org.palladiosimulator.metricspec.constants.MetricDescriptionConstants;
import org.palladiosimulator.monitorrepository.MeasurementSpecification;
import org.palladiosimulator.monitorrepository.MonitorRepository;
>>>>>>> 6cc4c84 (prepare merge)
import org.palladiosimulator.pcm.usagemodel.Start;
import org.palladiosimulator.pcm.usagemodel.Stop;
import org.palladiosimulator.pcm.usagemodel.UsageScenario;
import org.palladiosimulator.probeframework.calculator.Calculator;
import org.palladiosimulator.probeframework.calculator.DefaultCalculatorProbeSets;
import org.palladiosimulator.probeframework.calculator.IGenericCalculatorFactory;
import org.palladiosimulator.probeframework.measurement.RequestContext;

<<<<<<< HEAD
=======
import com.google.common.eventbus.Subscribe;

>>>>>>> 6cc4c84 (prepare merge)
/**
 * A usage model monitoring monitors each usage scenario in a usage model. For
 * that, every time a usage scenario has been
 * 
 * @author Julijan Katic
 *
 */
<<<<<<< HEAD
@OnEvent(when = MonitorModelVisited.class, then = CalculatorRegistered.class, cardinality = EventCardinality.SINGLE)
@OnEvent(when = UsageModelPassedElement.class, then = ProbeTaken.class, cardinality = EventCardinality.SINGLE)
@OnEvent(when = UsageModelPassedElement.class, then = ProbeTaken.class, cardinality = EventCardinality.SINGLE)
public class UsageScenarioResponseTimeMonitoringBehavior implements SimulationBehaviorExtension {
	
	private final IGenericCalculatorFactory calculatorFactory;
	private final Map<String, UserProbes> userProbesMap = new HashMap<>();
	
	@Inject
	public UsageScenarioResponseTimeMonitoringBehavior(final IGenericCalculatorFactory calculatorFactory) {
		this.calculatorFactory = calculatorFactory;
	}
	
	/**
	 * Creates probes if a measuring point for it was specified.
	 * @param event
	 * @return
	 */
	@Subscribe
	public Result onMeasurementSpecificationVisited(final MeasurementSpecificationVisited event) {
		final EObject eObject = EMFLoadHelper.loadAndResolveEObject(event.getMeasuringPoint().getResourceURIRepresentation());
		if (eObject instanceof UsageScenario) {
			final UsageScenario scenario = (UsageScenario) eObject;
			final UserProbes userProbes = new UserProbes();
			// TODO: Look at what MetricDescription was given
			
			this.userProbesMap.put(scenario.getId(), userProbes);
			final Calculator calculator = this.calculatorFactory.buildCalculator(MetricDescriptionConstants.RESPONSE_TIME_METRIC_TUPLE, event.getMeasuringPoint(),
						DefaultCalculatorProbeSets.createStartStopProbeConfiguration(userProbes.userStartedProbe, userProbes.userStoppedProbe));
			return Result.of(new CalculatorRegistered(calculator));
		} else {
			return Result.empty();
		}
	}
	
	@Subscribe(reified = Start.class)
	public Result onUsageScenarioStarted(final UsageModelPassedElement<Start> userStarted) {
		if (this.userProbesMap.containsKey(userStarted.getContext().getScenario().getId())) {
			final UserProbes userProbes = this.userProbesMap.get(userStarted.getContext().getScenario().getId());
			
			userProbes.userStartedProbe.takeMeasurement(userStarted);
			
			final ProbeTakenEntity entity = ProbeTakenEntity.builder()
					.withProbe(userProbes.userStartedProbe)
					.build();
			
			return Result.of(new ProbeTaken(entity));
		} else {
			return Result.empty();
		}
	}
	
	@Subscribe(reified = Stop.class)
	public Result onUsageScenarioFinished(final UsageModelPassedElement<Stop> userStopped) {
		if (this.userProbesMap.containsKey(userStopped.getContext().getScenario().getId())) {
			final UserProbes userProbes = this.userProbesMap.get(userStopped.getContext().getScenario().getId());
			userProbes.userStoppedProbe.takeMeasurement(userStopped);
			
			final ProbeTakenEntity entity = ProbeTakenEntity.builder()
					.withProbe(userProbes.userStoppedProbe)
					.build();
			
			return Result.of(new ProbeTaken(entity));
		} else {
			return Result.empty();
		}
	}
	
	
	private static final class UserProbes {
		private final EventCurrentSimulationTimeProbe userStartedProbe = new EventCurrentSimulationTimeProbe(UserProbes::passedElement);
		private final EventCurrentSimulationTimeProbe userStoppedProbe = new EventCurrentSimulationTimeProbe(UserProbes::passedElement);
	
		private static RequestContext passedElement(final DESEvent event) {
			if (event instanceof UsageModelPassedElement<?>) {
				final UsageModelPassedElement<?> el = (UsageModelPassedElement<?>) event;
=======
@OnEvent(when = MonitorModelVisited.class, whenReified = MeasurementSpecification.class, then = CalculatorRegistered.class, cardinality = EventCardinality.SINGLE)
@OnEvent(when = UsageModelPassedElement.class, whenReified = Start.class, then = ProbeTaken.class, cardinality = EventCardinality.SINGLE)
@OnEvent(when = UsageModelPassedElement.class, whenReified = Stop.class, then = ProbeTaken.class, cardinality = EventCardinality.SINGLE)
public class UsageScenarioResponseTimeMonitoringBehavior implements SimulationBehaviorExtension {

	private final IGenericCalculatorFactory calculatorFactory;
	private final Map<String, UserProbes> userProbesMap = new HashMap<>();

	@Inject
	public UsageScenarioResponseTimeMonitoringBehavior(
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
		if (eObject instanceof UsageScenario && MetricDescriptionUtility.metricDescriptionIdsEqual(measurementSpecification.getMetricDescription(),
				MetricDescriptionConstants.RESPONSE_TIME_METRIC)) {
			final UsageScenario scenario = (UsageScenario) eObject;
			final UserProbes userProbes = new UserProbes();
			this.userProbesMap.put(scenario.getId(), userProbes);
			
			final Calculator calculator = this.calculatorFactory.buildCalculator(
					MetricDescriptionConstants.RESPONSE_TIME_METRIC_TUPLE, measuringPoint,
					DefaultCalculatorProbeSets.createStartStopProbeConfiguration(userProbes.userStartedProbe,
							userProbes.userFinishedProbe));
				
			return ResultEvent.of(new CalculatorRegistered(calculator));
			}
		return ResultEvent.empty();
	}

	@Subscribe
	public ResultEvent<ProbeTaken> onUsageScenarioStarted(
			@Reified(Start.class) final UsageModelPassedElement<Start> userStarted) {
		if(this.userProbesMap.containsKey(userStarted.getContext().getScenario().getId())){
			final UserProbes userProbes = this.userProbesMap.get(userStarted.getContext().getScenario().getId());			
			userProbes.userStartedProbe.takeMeasurement(userStarted);
			return ResultEvent
					.of(new ProbeTaken(ProbeTakenEntity.builder().withProbe(userProbes.userStartedProbe).build()));
		}		
		return ResultEvent.empty();
	}

	@Subscribe
	public ResultEvent<ProbeTaken> onUsageScenarioFinished(
			@Reified(Stop.class) final UsageModelPassedElement<Stop> userStopped) {
		if(this.userProbesMap.containsKey(userStopped.getContext().getScenario().getId())){
		final UserProbes userProbes = this.userProbesMap.get(userStopped.getContext().getScenario().getId());
			userProbes.userFinishedProbe.takeMeasurement(userStopped);
			return ResultEvent
					.of(new ProbeTaken(ProbeTakenEntity.builder().withProbe(userProbes.userFinishedProbe).build()));
		}
		return ResultEvent.empty();
	}

	private static final class UserProbes {
		private final EventCurrentSimulationTimeProbe userStartedProbe = new EventCurrentSimulationTimeProbe(
				UserProbes::passedElement);
		private final EventCurrentSimulationTimeProbe userFinishedProbe = new EventCurrentSimulationTimeProbe(
				UserProbes::passedElement);

		private static RequestContext passedElement(final DESEvent desEvent) {
			if (desEvent instanceof UsageModelPassedElement<?>) {
				final UsageModelPassedElement<?> el = (UsageModelPassedElement<?>) desEvent;
>>>>>>> 6cc4c84 (prepare merge)
				return new RequestContext(el.getContext().getUser().getId());
			}
			return RequestContext.EMPTY_REQUEST_CONTEXT;
		}
	}
<<<<<<< HEAD
	
=======
>>>>>>> 6cc4c84 (prepare merge)
}
