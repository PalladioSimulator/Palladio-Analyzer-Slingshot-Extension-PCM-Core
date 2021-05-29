package org.palladiosimulator.analyzer.slingshot.monitor.recorder;

import java.util.HashMap;
import java.util.Map;

import org.palladiosimulator.edp2.models.measuringpoint.MeasuringPoint;
import org.palladiosimulator.metricspec.MetricDescription;
import org.palladiosimulator.probeframework.calculator.Calculator;
import org.palladiosimulator.probeframework.calculator.CalculatorProbeSet;
import org.palladiosimulator.probeframework.calculator.IGenericCalculatorFactory;
import org.palladiosimulator.recorderframework.IRecorder;
import org.palladiosimulator.recorderframework.config.AbstractRecorderConfiguration;
import org.palladiosimulator.recorderframework.config.IRecorderConfiguration;
import org.palladiosimulator.recorderframework.config.IRecorderConfigurationFactory;
import org.palladiosimulator.recorderframework.utils.RecorderExtensionHelper;

/*
 * Taken from https://github.com/PalladioSimulator/Palladio-Analyzer-SimuCom/blob/75a44715be3c78ac7e1c9eb2b28eba8633398b18/bundles/de.uka.ipd.sdq.simucomframework/src/de/uka/ipd/sdq/simucomframework/calculator/RecorderAttachingCalculatorFactoryDecorator.java
 */
/**
 * Factory class to create {@link Calculator}s used in a SimuCom simulation run.
 * 
 * @author Steffen Becker, Phillip Merkle, Sebastian Lehrig
 *
 */
public class RecorderAttachingCalculatorFactoryDecorator implements IGenericCalculatorFactory {

	/**
	 * SimuCom model which is simulated.
	 */
	private final IGenericCalculatorFactory decoratedCalculatorFactory;
	private final String recorderName;
	private final IRecorderConfigurationFactory configurationFactory;

	public RecorderAttachingCalculatorFactoryDecorator(final IGenericCalculatorFactory decoratedCalculatorFactory,
			final String recorderName, final IRecorderConfigurationFactory configurationFactory) {
		this.decoratedCalculatorFactory = decoratedCalculatorFactory;
		this.recorderName = recorderName;
		this.configurationFactory = configurationFactory;
	}

	@Override
	public Calculator buildCalculator(final MetricDescription metric, final MeasuringPoint measuringPoint,
			final CalculatorProbeSet probeConfiguration) {
		return this.setupRecorder(
				this.decoratedCalculatorFactory.buildCalculator(metric, measuringPoint, probeConfiguration));
	}

	private Calculator setupRecorder(final Calculator calculator) {
		final Map<String, Object> recorderConfigurationMap = new HashMap<String, Object>();
		recorderConfigurationMap.put(AbstractRecorderConfiguration.RECORDER_ACCEPTED_METRIC,
				calculator.getMetricDesciption());
		recorderConfigurationMap.put(AbstractRecorderConfiguration.MEASURING_POINT, calculator.getMeasuringPoint());

		final IRecorder recorder = RecorderExtensionHelper
				.instantiateRecorderImplementationForRecorder(this.recorderName);
		final IRecorderConfiguration recorderConfiguration = this.configurationFactory
				.createRecorderConfiguration(recorderConfigurationMap);
		recorder.initialize(recorderConfiguration);
		calculator.addObserver(recorder);
		return calculator;
	}

}
