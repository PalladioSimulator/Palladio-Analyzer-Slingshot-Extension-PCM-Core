package org.palladiosimulator.analyzer.slingshot.monitor;

import org.palladiosimulator.analyzer.slingshot.monitor.data.MeasurementMade;
import org.palladiosimulator.analyzer.slingshot.monitor.data.ProcessingTypeListener;
import org.palladiosimulator.analyzer.slingshot.simulation.api.SimulationScheduling;
import org.palladiosimulator.measurementframework.MeasuringValue;
import org.palladiosimulator.measurementframework.listener.IMeasurementSourceListener;
import org.palladiosimulator.monitorrepository.ProcessingType;

/**
 * A ProcessingTypeMeasurementSourceListener is a wrapper that delegates a
 * {@link ProcessingTypeListener} and "converts" the Observer pattern into an
 * event handler.
 * <p>
 * For some {@link ProcessingType}s, there is a need to listen to a new
 * measurement and convert that measurement to something else (i.e., aggregate
 * multiple measurements into an average measurement). In order to remain
 * event-driven, the {@link ProcessingTypeListener}s will simply "listen" to a
 * {@link MeasurementMade} event and convert the measurements, which will then
 * return new events. These returned events are then published in Slingshot.
 * <p>
 * The {@link ProcessingTypeListener}s, however, are not directly part of the
 * Behavior Extensions, because otherwise ALL type listeners would be called,
 * regardless whether they belong to the calculator that made the measurement.
 * <p>
 * Hence, this wrapper can be added to a calculator as a
 * {@link IMeasurementSourceListener}, call the delegate and simply publish the
 * returned events from
 * {@link ProcessingTypeListener#onMeasurementMade(MeasurementMade)}.
 * 
 * @author Julijan Katic
 *
 */
public final class ProcessingTypeMeasurementSourceListener implements IMeasurementSourceListener {

	/**
	 * The delegate which is like a IMeasurementSourceListener in an event-driven
	 * way.
	 */
	private final ProcessingTypeListener delegate;

	/** The scheduling which is needed to publish events from the outside. */
	private final SimulationScheduling scheduling;

	/**
	 * Constructs a ProcessingTypeMeasurementSourceListener.
	 * 
	 * @param scheduling The scheduling which is needed to publish events from the
	 *                   outside.
	 * @param delegate   The delegate which is like a IMeasurementSourceListener in
	 *                   an event-driven way.
	 */
	public ProcessingTypeMeasurementSourceListener(
			final SimulationScheduling scheduling,
			final ProcessingTypeListener delegate) {
		this.delegate = delegate;
		this.scheduling = scheduling;
	}

	/**
	 * Calls the delegate's
	 * {@link ProcessingTypeListener#onMeasurementMade(MeasurementMade)} method and
	 * publishes each event into the event system.
	 */
	@Override
	public void newMeasurementAvailable(final MeasuringValue newMeasurement) {
		this.delegate.onMeasurementMade(new MeasurementMade(newMeasurement))
				.getEventsForScheduling()
				.forEach(this.scheduling::scheduleForSimulation);
	}

	/**
	 * {@inheritDoc} This is done by calling the delegate's
	 * {@link ProcessingTypeListener#preUnregister()} method.
	 */
	@Override
	public void preUnregister() {
		this.delegate.preUnregister();
	}

}
