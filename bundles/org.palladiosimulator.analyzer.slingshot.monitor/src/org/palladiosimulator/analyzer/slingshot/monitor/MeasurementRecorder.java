package org.palladiosimulator.analyzer.slingshot.monitor;

import java.util.LinkedList;
import java.util.List;

import org.palladiosimulator.measurementframework.MeasuringValue;
import org.palladiosimulator.recorderframework.IRecorder;
import org.palladiosimulator.recorderframework.config.IRecorderConfiguration;

public abstract class MeasurementRecorder implements IRecorder {

	private final List<MeasuringValue> values = new LinkedList<>();

	@Override
	public void newMeasurementAvailable(final MeasuringValue arg0) {

	}

	@Override
	public void preUnregister() {
		// TODO Auto-generated method stub

	}

	@Override
	public void flush() {
	}

	@Override
	public void initialize(final IRecorderConfiguration arg0) {
	}

	@Override
	public void writeData(final MeasuringValue arg0) {
		this.newMeasurementAvailable(arg0);
	}

}
