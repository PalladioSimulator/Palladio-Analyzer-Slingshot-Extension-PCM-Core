package org.palladiosimulator.analyzer.slingshot.monitor;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.palladiosimulator.analyzer.slingshot.monitor.data.MeasurementMade;
import org.palladiosimulator.analyzer.slingshot.simulation.api.SimulationDriver;
import org.palladiosimulator.measurementframework.MeasuringValue;
import org.palladiosimulator.measurementframework.listener.IMeasurementSourceListener;

/**
 * This class allows the simulator to be notified when new measurements are
 * available.
 * 
 * @author Julijan Katic
 */
@Singleton
public final class MonitoringBehavior implements IMeasurementSourceListener {

	private final SimulationDriver driver;

	@Inject
	public MonitoringBehavior(final SimulationDriver driver) {
		this.driver = driver;
	}

	@Override
	public void newMeasurementAvailable(final MeasuringValue newMeasurement) {
		this.driver.scheduleForSimulation(new MeasurementMade(newMeasurement));
	}

	@Override
	public void preUnregister() {
		// do nothing
	}

}
