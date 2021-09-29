package org.palladiosimulator.analyzer.slingshot.monitor.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.palladiosimulator.edp2.models.measuringpoint.MeasuringPoint;
import org.palladiosimulator.edp2.util.MetricDescriptionUtility;
import org.palladiosimulator.measurementframework.listener.IMeasurementSourceListener;
import org.palladiosimulator.metricspec.BaseMetricDescription;
import org.palladiosimulator.metricspec.MetricDescription;
import org.palladiosimulator.probeframework.calculator.Calculator;
import org.palladiosimulator.probeframework.calculator.CalculatorRegistryListener;
import org.palladiosimulator.probeframework.calculator.IObservableCalculatorRegistry;

/**
 * This facade allows to defer registrations for measurements which are
 * initialized lazily during simulation.
 * 
 * Currently it depends on the existence of a
 * {@code RegisterCalculatorFactoryDecorator} to which a listener for a new
 * calculators is attached.
 * 
 * @author Julijan Katic
 *
 */
public abstract class DeferredMeasurementInitialization {

	private static final Logger LOGGER = Logger.getLogger(DeferredMeasurementInitialization.class);

	private static final class DeferredMeasurementInitializationImpl extends DeferredMeasurementInitialization
			implements CalculatorRegistryListener {

		private final IObservableCalculatorRegistry registryAccess;
		private final Map<String, Map<MetricDescription, Set<Supplier<IMeasurementSourceListener>>>> deferredInitializations = new HashMap<>();

		/**
		 * This constructor is private as it is not supposed to be used directly.
		 */
		private DeferredMeasurementInitializationImpl(final IObservableCalculatorRegistry registryAccess) {
			this.registryAccess = registryAccess;
		}

		@Override
		public void notifyCalculatorRegistration(final Calculator calculator) {
			synchronized (this.deferredInitializations) {
				if (this.deferredInitializations
						.containsKey(calculator.getMeasuringPoint().getStringRepresentation())) {
					final Map<MetricDescription, Set<Supplier<IMeasurementSourceListener>>> callbacks = this.deferredInitializations
							.get(calculator.getMeasuringPoint().getStringRepresentation());
					callbacks.keySet().stream()
							.filter(metricDesc -> {
								if (metricDesc.getId().equals(calculator.getMetricDesciption().getId())
										|| (metricDesc instanceof BaseMetricDescription && MetricDescriptionUtility
												.isBaseMetricDescriptionSubsumedByMetricDescription(
														(BaseMetricDescription) metricDesc,
														calculator.getMetricDesciption()))) {
									this.deferredInitializations
											.get(calculator.getMeasuringPoint().getStringRepresentation())
											.get(metricDesc).stream().map(Supplier::get)
											.forEach(calculator::addObserver);
									return true;
								}
								return false;
							})
							.collect(Collectors.toList())
							.forEach(callbacks::remove);

					if (callbacks.isEmpty()) {
						this.deferredInitializations.remove(calculator.getMeasuringPoint().getStringRepresentation());
					}
					if (this.deferredInitializations.isEmpty()) {
						this.registryAccess.removeObserver(this);
					}
				}
			}
		}

		@Override
		public void onMetricDescriptionAndMeasuringPoint(final MetricDescription desc, final MeasuringPoint mp,
				final Supplier<IMeasurementSourceListener> supplier) {
			Objects.requireNonNull(desc);
			Objects.requireNonNull(mp);
			Objects.requireNonNull(supplier);

			synchronized (this.deferredInitializations) {
				final Optional<Calculator> baseCalculator = this.getBaseCalculator(desc, mp);
				if (baseCalculator.isPresent()) {
					baseCalculator.get().addObserver(supplier.get());
				} else {
					if (this.deferredInitializations.isEmpty()) {
						if (this.registryAccess.getObservers().contains(this)) {
							LOGGER.warn(String.format("Deferred initialization is already registered as a listener."
									+ "Metric: %s Measuring Point: %s", desc.getName(), mp.getStringRepresentation()));
						} else {
							this.registryAccess.addObserver(this);
						}
					}

					this.deferredInitializations
							.computeIfAbsent(mp.getStringRepresentation(), s -> new HashMap<>())
							.computeIfAbsent(desc, d -> new HashSet<>())
							.add(supplier);
				}
			}
		}

		private Optional<Calculator> getBaseCalculator(final MetricDescription metric,
				final MeasuringPoint measuringPoint) {
			final Calculator baseCalculator = this.registryAccess
					.getCalculatorByMeasuringPointAndMetricDescription(measuringPoint, metric);
			if (baseCalculator == null && metric instanceof BaseMetricDescription) {
				return this.registryAccess.getCalculatorsForMeasuringPoint(measuringPoint).stream()
						.filter(calc -> MetricDescriptionUtility.isBaseMetricDescriptionSubsumedByMetricDescription(
								(BaseMetricDescription) metric, calc.getMetricDesciption()))
						.findAny();
			}
			return Optional.ofNullable(baseCalculator);
		}
	}

	/**
	 * Returns the facade to be used to defer recorder registrations. This operation
	 * ensures that there is only one registration facade per
	 * {@code RegisterCalculatorFactoryDecorator}.
	 * 
	 * @param registryAccess The decorated calculator factory.
	 * @return the appropriate registration facade.
	 */
	public static DeferredMeasurementInitialization forCalculatorFactoryDecorator(
			final IObservableCalculatorRegistry registryAccess) {
		return registryAccess.getObservers().stream()
				.filter(DeferredMeasurementInitializationImpl.class::isInstance)
				.map(DeferredMeasurementInitializationImpl.class::cast)
				.filter(dmi -> dmi.registryAccess.equals(registryAccess))
				.findAny()
				.orElseGet(() -> new DeferredMeasurementInitializationImpl(registryAccess));
	}

	/**
	 * Registers a provider of an {@code IMeasurementSourceListener}. The listener
	 * is requested once a calculator which fits to {@code desc} and {@code mp} is
	 * registered. If the calculator is already registered, the provider is
	 * requested directly.
	 * 
	 * If {@code desc} is of type {@code BaseMetricDescription}, the provided
	 * listener will also be registered if a calculator provides measurements which
	 * contain the {@code BaseMetricDescription}.
	 * 
	 * @param desc     The metric description of the measurements produced by the
	 *                 required calculator.
	 * @param mp       THe measuring point of the measurements produced by the
	 *                 required calculator.
	 * @param supplier A provider of the listener, e.g. a chained recorder.
	 */
	public abstract void onMetricDescriptionAndMeasuringPoint(final MetricDescription desc, final MeasuringPoint mp,
			Supplier<IMeasurementSourceListener> supplier);
}
