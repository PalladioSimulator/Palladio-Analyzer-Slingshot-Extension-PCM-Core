package org.palladiosimulator.analyzer.slingshot.behavior.usagesimulation.monitor;

import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UserFinished;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UserStarted;
import org.palladiosimulator.analyzer.slingshot.monitor.calculator.AbstractCalculatorProviders;
import org.palladiosimulator.analyzer.slingshot.monitor.calculator.CalculatorProvider;
import org.palladiosimulator.analyzer.slingshot.monitor.probe.Probed;
import org.palladiosimulator.analyzer.slingshot.monitor.probes.EventCurrentSimulationTimeProbe;
import org.palladiosimulator.edp2.models.measuringpoint.MeasuringPointRepository;
import org.palladiosimulator.metricspec.constants.MetricDescriptionConstants;
import org.palladiosimulator.pcm.usagemodel.UsageModel;
import org.palladiosimulator.pcmmeasuringpoint.PcmmeasuringpointFactory;
import org.palladiosimulator.pcmmeasuringpoint.UsageScenarioMeasuringPoint;
import org.palladiosimulator.probeframework.calculator.Calculator;
import org.palladiosimulator.probeframework.calculator.DefaultCalculatorProbeSets;
import org.palladiosimulator.probeframework.calculator.IGenericCalculatorFactory;

import com.google.inject.Inject;

public final class UsageSimulationCalculatorProviders extends AbstractCalculatorProviders {

	private final UsageModel usageModel;
	private final MeasuringPointRepository repository;

	@Inject
	public UsageSimulationCalculatorProviders(final IGenericCalculatorFactory calculatorFactory,
			final UsageModel usageModel, final MeasuringPointRepository measuringPointRepository) {
		super(calculatorFactory);
		this.usageModel = usageModel;
		this.repository = measuringPointRepository;
	}

	@CalculatorProvider(requestContextMapper = UserToRequestContextMapper.class)
	public Calculator userVisitTimeCalculator(
			@Probed(UserStarted.class) final EventCurrentSimulationTimeProbe<UserStarted> userStartedProbe,
			@Probed(UserFinished.class) final EventCurrentSimulationTimeProbe<UserFinished> userFinishedProbe) {

		final UsageScenarioMeasuringPoint measuringPoint = PcmmeasuringpointFactory.eINSTANCE
				.createUsageScenarioMeasuringPoint();
		measuringPoint.setUsageScenario(
				this.usageModel.getUsageScenario_UsageModel().get(0));

		this.initializeMeasuringPoint(measuringPoint, this.repository);

		return this.getCalculatorFactory().buildCalculator(MetricDescriptionConstants.RESPONSE_TIME_METRIC_TUPLE,
				measuringPoint,
				DefaultCalculatorProbeSets.createStartStopProbeConfiguration(userStartedProbe, userFinishedProbe));
	}

}
