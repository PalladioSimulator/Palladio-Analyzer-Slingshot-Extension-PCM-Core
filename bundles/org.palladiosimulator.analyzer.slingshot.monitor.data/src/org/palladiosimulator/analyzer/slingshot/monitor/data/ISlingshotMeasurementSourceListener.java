package org.palladiosimulator.analyzer.slingshot.monitor.data;

public interface ISlingshotMeasurementSourceListener {
	
	public void newSlingshotMeasurementAvailable(final SlingshotMeasuringValue newMeasuringValue);

	public void preUnregister();
	
}
