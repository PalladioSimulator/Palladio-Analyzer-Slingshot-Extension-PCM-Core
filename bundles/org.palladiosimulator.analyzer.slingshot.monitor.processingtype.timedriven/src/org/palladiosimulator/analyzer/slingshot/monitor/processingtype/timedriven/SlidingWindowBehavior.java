package org.palladiosimulator.analyzer.slingshot.monitor.processingtype.timedriven;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import javax.inject.Inject;
import javax.measure.Measure;
import javax.measure.unit.SI;

import org.palladiosimulator.analyzer.slingshot.monitor.data.CalculatorRegistered;
import org.palladiosimulator.analyzer.slingshot.monitor.data.MonitorModelVisited;
import org.palladiosimulator.analyzer.slingshot.simulation.api.SimulationScheduling;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.SimulationBehaviorExtension;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.OnEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.Reified;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.results.ResultEvent;
import org.palladiosimulator.edp2.models.measuringpoint.MeasuringPoint;
import org.palladiosimulator.experimentanalysis.DiscardAllElementsPriorToLowerBoundStrategy;
import org.palladiosimulator.experimentanalysis.ISlidingWindowMoveOnStrategy;
import org.palladiosimulator.experimentanalysis.KeepLastElementPriorToLowerBoundStrategy;
import org.palladiosimulator.experimentanalysis.SlidingWindow;
import org.palladiosimulator.experimentanalysis.SlidingWindowRecorder;
import org.palladiosimulator.metricspec.MetricDescription;
import org.palladiosimulator.metricspec.MetricSpecPackage;
import org.palladiosimulator.metricspec.NumericalBaseMetricDescription;
import org.palladiosimulator.metricspec.ScopeOfValidity;
import org.palladiosimulator.monitorrepository.MeasurementSpecification;
import org.palladiosimulator.monitorrepository.TimeDrivenAggregation;
import org.palladiosimulator.probeframework.calculator.Calculator;

import com.google.common.eventbus.Subscribe;

@OnEvent(when = MonitorModelVisited.class, whenReified = TimeDrivenAggregation.class, then = {})
@OnEvent(when = CalculatorRegistered.class, then = {})
public class SlidingWindowBehavior implements SimulationBehaviorExtension {

	private final ISlidingWindowMoveOnStrategy discreteMetricScopeStrategy = new DiscardAllElementsPriorToLowerBoundStrategy();
	private final ISlidingWindowMoveOnStrategy continuousMetricScopeStrategy = new KeepLastElementPriorToLowerBoundStrategy();

	private final Map<CalculatorDescription, List<Consumer<Calculator>>> deferredCalculatorInitializations = new HashMap<>();

	private final SimulationScheduling scheduling;

	@Inject
	public SlidingWindowBehavior(final SimulationScheduling scheduling) {
		this.scheduling = scheduling;
	}

	@Subscribe
	public ResultEvent<DESEvent> onTimeDrivenAggregation(
			@Reified(TimeDrivenAggregation.class) final MonitorModelVisited<TimeDrivenAggregation> timeDrivenAggregation) {
		this.initAggregatorForMeasSpec(timeDrivenAggregation.getModelElement().getMeasurementSpecification());
		return ResultEvent.empty();
	}

	@Subscribe
	public ResultEvent<DESEvent> onCalculatorRegistered(final CalculatorRegistered calculatorRegistered) {
		final List<Consumer<Calculator>> initializations = this.deferredCalculatorInitializations
				.get(new CalculatorDescription(
						calculatorRegistered.getEntity().getMeasuringPoint().getStringRepresentation(),
						calculatorRegistered.getEntity().getMetricDesciption()));

		if (initializations != null) {
			initializations.forEach(consumer -> consumer.accept(calculatorRegistered.getEntity()));
			this.deferredCalculatorInitializations.remove(new CalculatorDescription(
					calculatorRegistered.getEntity().getMeasuringPoint().getStringRepresentation(),
					calculatorRegistered.getEntity().getMetricDesciption()));
		}

		return ResultEvent.empty();
	}

	private void initAggregatorForMeasSpec(final MeasurementSpecification measurementSpecification) {
		final MeasuringPoint measuringPoint = measurementSpecification.getMonitor().getMeasuringPoint();
		if (!MetricSpecPackage.Literals.NUMERICAL_BASE_METRIC_DESCRIPTION
				.isInstance(measurementSpecification.getMetricDescription())) {
			throw new IllegalStateException(
					"Time driven aggregation of measurements (sliding window based) cannot be initialized:\n"
							+ "Currently, only "
							+ MetricSpecPackage.Literals.NUMERICAL_BASE_METRIC_DESCRIPTION.getName()
							+ "s are supported!");
		}

		final NumericalBaseMetricDescription expectedMetric = (NumericalBaseMetricDescription) measurementSpecification
				.getMetricDescription();

		this.deferredCalculatorInitializations
				.computeIfAbsent(new CalculatorDescription(measuringPoint.getStringRepresentation(), expectedMetric),
						desc -> new LinkedList<>())
				.add(calculator -> this.register(calculator, measurementSpecification, expectedMetric, measuringPoint));
	}

	private void register(final Calculator calculator, final MeasurementSpecification spec,
			final NumericalBaseMetricDescription expectedMetric, final MeasuringPoint measuringPoint) {
		final ISlidingWindowMoveOnStrategy moveOnStrategy = expectedMetric
				.getScopeOfValidity() == ScopeOfValidity.CONTINUOUS ? this.continuousMetricScopeStrategy
						: this.discreteMetricScopeStrategy;

		final TimeDrivenAggregation aggregation = (TimeDrivenAggregation) spec.getProcessingType();
		final SlidingWindow window = new SlingshotSlidingWindow(aggregation.getWindowLengthAsMeasure(),
				aggregation.getWindowIncrementAsMeasure(), Measure.valueOf(0.0d, SI.SECOND), expectedMetric,
				moveOnStrategy);
		final SlidingWindowStatisticalCharacterizationAggregator windowAggregator = new SlidingWindowStatisticalCharacterizationAggregator(
				aggregation.getStatisticalCharacterization().getAggregator(expectedMetric));

		if (spec.isTriggersSelfAdaptations()) {
			windowAggregator.addRecorder(new SlidingWindowRuntimeMeasurementsRecorder(aggregation, measuringPoint,
					expectedMetric, this.scheduling));
		}

		calculator.addObserver(new SlidingWindowRecorder(window, windowAggregator));
	}

	private static final class CalculatorDescription {
		private final String measuringPointStringRepresentation;
		private final MetricDescription metricDescription;

		private CalculatorDescription(final String measuringPointStringRepresentation,
				final MetricDescription metricDescription) {
			this.measuringPointStringRepresentation = measuringPointStringRepresentation;
			this.metricDescription = metricDescription;
		}

		@Override
		public int hashCode() {
			return Objects.hash(this.measuringPointStringRepresentation, this.metricDescription);
		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj) {
				return true;
			}
			if (!(obj instanceof CalculatorDescription)) {
				return false;
			}
			final CalculatorDescription other = (CalculatorDescription) obj;
			return Objects.equals(this.measuringPointStringRepresentation, other.measuringPointStringRepresentation)
					&& Objects.equals(this.metricDescription, other.metricDescription);
		}

	}
}
