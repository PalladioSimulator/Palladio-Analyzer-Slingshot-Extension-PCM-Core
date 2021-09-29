package org.palladiosimulator.analyzer.slingshot.monitor;

import org.palladiosimulator.analyzer.slingshot.monitor.data.MeasurementMade;
import org.palladiosimulator.analyzer.slingshot.monitor.data.ProcessingTypeListener;
import org.palladiosimulator.analyzer.slingshot.simulation.api.SimulationScheduling;
import org.palladiosimulator.measurementframework.MeasuringValue;
import org.palladiosimulator.measurementframework.listener.IMeasurementSourceListener;

public final class ProcessingTypeMeasurementSourceListener implements IMeasurementSourceListener {

	private final ProcessingTypeListener delegate;
	private final SimulationScheduling scheduling;

	public ProcessingTypeMeasurementSourceListener(
			final SimulationScheduling scheduling,
			final ProcessingTypeListener delegate) {
		this.delegate = delegate;
		this.scheduling = scheduling;
	}

	@Override
	public void newMeasurementAvailable(final MeasuringValue newMeasurement) {
		this.delegate.onMeasurementMade(new MeasurementMade(newMeasurement))
				.getEventsForScheduling()
				.forEach(this.scheduling::scheduleForSimulation);
	}

	@Override
	public void preUnregister() {
		this.delegate.preUnregister();
	}

}
