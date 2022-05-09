package org.palladiosimulator.analyzer.slingshot.monitor.calculator;

import org.palladiosimulator.analyzer.slingshot.monitor.data.ISlingshotMeasurementSourceListener;
import org.palladiosimulator.analyzer.slingshot.monitor.data.SlingshotMeasuringValue;
import org.palladiosimulator.commons.designpatterns.AbstractObservable;
import org.palladiosimulator.commons.designpatterns.IAbstractObservable;
import org.palladiosimulator.metricspec.metricentity.IMetricEntity;
import org.palladiosimulator.metricspec.metricentity.MetricEntity;
import org.palladiosimulator.probeframework.calculator.Calculator;
import org.palladiosimulator.probeframework.measurement.ProbeMeasurement;
import org.palladiosimulator.probeframework.probes.listener.IProbeListener;

public class SlingshotCalculator extends MetricEntity implements IAbstractObservable<ISlingshotMeasurementSourceListener>, IProbeListener {

	private final AbstractObservable<ISlingshotMeasurementSourceListener> observableDelegate = new AbstractObservable<ISlingshotMeasurementSourceListener>() {
	};
	
	public SlingshotCalculator(final Calculator actualCalculator) {
		
	}
	
	@Override
	public void addObserver(ISlingshotMeasurementSourceListener observer) {
		this.observableDelegate.addObserver(observer);
	}

	@Override
	public void removeObserver(ISlingshotMeasurementSourceListener observer) {
		this.observableDelegate.removeObserver(observer);
	}
	
	private void notifyMeasurementSourceListener(final SlingshotMeasuringValue measuringValue) {
		if (!isCompatibleMeasurement(measuringValue)) {
			throw new IllegalArgumentException("Taken measurement has an incompatible metric");
		}
		observableDelegate.getEventDispatcher().newSlingshotMeasurementAvailable(measuringValue);
	}
	
	private boolean isCompatibleMeasurement(final IMetricEntity measurement) {
		return isCompatibleWith(measurement.getMetricDesciption());
	}
	
	@Override
	public void newProbeMeasurementAvailable(ProbeMeasurement probeMeasurement) {
		// TODO Auto-generated method stub
		
	}
}
