package org.palladiosimulator.analyzer.slingshot.monitor.util;

import org.palladiosimulator.analyzer.slingshot.monitor.data.SlingshotMeasuringValue;
import org.palladiosimulator.edp2.models.measuringpoint.MeasuringPoint;
import org.palladiosimulator.measurementframework.MeasuringValue;
import org.palladiosimulator.measurementframework.listener.IMeasurementSourceListener;
import org.palladiosimulator.probeframework.calculator.Calculator;

public final class SlingshotCalculatorWrapper {

	public static IMeasurementSourceListener wrap(final Calculator calculator, final IMeasurementSourceListener delegate) {
		return wrap(calculator.getMeasuringPoint(), delegate);
	}
	
	public static IMeasurementSourceListener wrap(final MeasuringPoint measuringPoint, final IMeasurementSourceListener delegate) {
		return new IMeasurementSourceListener() {
			
			public void preUnregister() {
				delegate.preUnregister();
			}
			
			public void newMeasurementAvailable(MeasuringValue newMeasurement) {
				final SlingshotMeasuringValue measuringValue = new SlingshotMeasuringValue(newMeasurement, measuringPoint);
				delegate.newMeasurementAvailable(measuringValue); 
			}
		};
	}
	
}
