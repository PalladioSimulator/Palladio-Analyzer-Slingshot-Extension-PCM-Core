package org.palladiosimulator.analyzer.slingshot.monitor.calculator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import javax.inject.Inject;

import org.palladiosimulator.analyzer.slingshot.monitor.ProcessingTypeMeasurementSourceListener;
import org.palladiosimulator.analyzer.slingshot.monitor.data.CalculatorRegistered;
import org.palladiosimulator.analyzer.slingshot.monitor.data.ProcessingTypeRevealed;
import org.palladiosimulator.analyzer.slingshot.simulation.api.SimulationScheduling;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.SimulationBehaviorExtension;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.results.ResultEvent;
import org.palladiosimulator.edp2.models.measuringpoint.MeasuringPoint;
import org.palladiosimulator.edp2.util.MetricDescriptionUtility;
import org.palladiosimulator.measurementframework.listener.IMeasurementSourceListener;
import org.palladiosimulator.metricspec.BaseMetricDescription;
import org.palladiosimulator.metricspec.MetricDescription;
import org.palladiosimulator.probeframework.calculator.Calculator;
import org.palladiosimulator.probeframework.calculator.IObservableCalculatorRegistry;

import com.google.common.eventbus.Subscribe;

public class DeferredCalculatorMeasurementInitializationBehavior implements SimulationBehaviorExtension {

	private final Map<String, Map<MetricDescription, Set<Supplier<IMeasurementSourceListener>>>> sourceListener = new HashMap<>();

	private final IObservableCalculatorRegistry registry;
	private final SimulationScheduling scheduling;

	@Inject
	public DeferredCalculatorMeasurementInitializationBehavior(
			final IObservableCalculatorRegistry registry,
			final SimulationScheduling scheduling) {
		this.registry = registry;
		this.scheduling = scheduling;
	}

	@Subscribe
	public ResultEvent<?> onCalculatorRegistered(final CalculatorRegistered register) {
		if (this.sourceListener.containsKey(register.getEntity().getMeasuringPoint().getStringRepresentation())) {
			final Map<MetricDescription, Set<Supplier<IMeasurementSourceListener>>> callbacks = this.sourceListener
					.get(register.getEntity().getMeasuringPoint().getStringRepresentation());
			callbacks.keySet().stream()
					.filter(metricDesc -> {
						if (metricDesc.getId().equals(register.getEntity().getMetricDesciption().getId())
								|| (metricDesc instanceof BaseMetricDescription
										&& MetricDescriptionUtility.isBaseMetricDescriptionSubsumedByMetricDescription(
												(BaseMetricDescription) metricDesc,
												register.getEntity().getMetricDesciption()))) {
							callbacks.get(metricDesc).stream()
									.map(Supplier::get)
									.forEach(source -> register.getEntity().addObserver(source));
							return true;
						}
						return false;
					})
					.forEach(callbacks::remove);
			if (callbacks.isEmpty()) {
				this.sourceListener.remove(register.getEntity().getMeasuringPoint().getStringRepresentation());
			}
		}

		return ResultEvent.empty();
	}

	@Subscribe
	public ResultEvent<?> onNewProcessingTypeAvailable(final ProcessingTypeRevealed processingTypeRevealed) {
		final Optional<Calculator> baseCalculator = this.getBaseCalculator(
				processingTypeRevealed.getMetricDescription(), processingTypeRevealed.getMeasuringPoint());
		if (baseCalculator.isPresent()) {
			baseCalculator.get().addObserver(
					new ProcessingTypeMeasurementSourceListener(this.scheduling,
							processingTypeRevealed.getMeasurementSourceListener()));
		} else {
			this.sourceListener.computeIfAbsent(processingTypeRevealed.getMeasuringPoint().getStringRepresentation(),
					s -> new HashMap<>())
					.computeIfAbsent(processingTypeRevealed.getMetricDescription(), d -> new HashSet<>())
					.add(() -> new ProcessingTypeMeasurementSourceListener(this.scheduling,
							processingTypeRevealed.getMeasurementSourceListener()));
		}

		return ResultEvent.empty();
	}

	private Optional<Calculator> getBaseCalculator(final MetricDescription metricDescription, final MeasuringPoint mp) {
		final Calculator baseCalculator = this.registry.getCalculatorByMeasuringPointAndMetricDescription(mp,
				metricDescription);
		if (baseCalculator == null && metricDescription instanceof BaseMetricDescription) {
			return this.registry.getCalculatorsForMeasuringPoint(mp).stream()
					.filter(calc -> MetricDescriptionUtility.isBaseMetricDescriptionSubsumedByMetricDescription(
							(BaseMetricDescription) metricDescription, calc.getMetricDesciption()))
					.findAny();
		}
		return Optional.ofNullable(baseCalculator);
	}
}
