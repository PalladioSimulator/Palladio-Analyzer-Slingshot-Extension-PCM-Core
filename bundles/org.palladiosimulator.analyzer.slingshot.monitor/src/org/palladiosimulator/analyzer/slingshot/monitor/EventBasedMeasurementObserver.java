package org.palladiosimulator.analyzer.slingshot.monitor;

import javax.inject.Inject;

import org.palladiosimulator.analyzer.slingshot.monitor.data.MeasurementMade;
import org.palladiosimulator.analyzer.slingshot.simulation.api.SimulationScheduling;
import org.palladiosimulator.measurementframework.MeasuringValue;
import org.palladiosimulator.measurementframework.listener.IMeasurementSourceListener;

/**
 * This class will "convert" the measurement source listener to an event-based
 * notifier. It listens whether a new measurement is available
 * ({@link MeasuringValue}) and publish the {@link MeasurementMade} event.
 * <p>
 * This approach is needed since the probe framework is using an observer
 * pattern.
 * 
 * @author Julijan Katic
 */
public class EventBasedMeasurementObserver implements IMeasurementSourceListener {

	private final SimulationScheduling simulationScheduling;

	@Inject
	public EventBasedMeasurementObserver(final SimulationScheduling simulationScheduling) {
		this.simulationScheduling = simulationScheduling;
	}

	@Override
	public void newMeasurementAvailable(final MeasuringValue arg0) {
		this.simulationScheduling.scheduleForSimulation(new MeasurementMade(arg0));
	}

	@Override
	public void preUnregister() {
		// do nothing.
	}

}