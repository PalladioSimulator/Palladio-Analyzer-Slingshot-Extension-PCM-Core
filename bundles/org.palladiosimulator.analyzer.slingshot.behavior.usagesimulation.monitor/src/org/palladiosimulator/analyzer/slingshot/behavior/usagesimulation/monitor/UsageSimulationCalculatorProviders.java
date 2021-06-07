package org.palladiosimulator.analyzer.slingshot.behavior.usagesimulation.monitor;

import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UserFinished;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UserStarted;
import org.palladiosimulator.analyzer.slingshot.monitor.probe.Probed;
import org.palladiosimulator.analyzer.slingshot.monitor.probes.EventCurrentSimulationTimeProbe;
import org.palladiosimulator.metricspec.constants.MetricDescriptionConstants;
import org.palladiosimulator.probeframework.calculator.Calculator;
import org.palladiosimulator.probeframework.calculator.DefaultCalculatorProbeSets;
import org.palladiosimulator.probeframework.calculator.IGenericCalculatorFactory;

public final class UsageSimulationCalculatorProviders {

	public static Calculator userVisitTimeCalculator(
			final IGenericCalculatorFactory calculatorFactory,
			@Probed(UserStarted.class) final EventCurrentSimulationTimeProbe<UserStarted> userStartedProbe,
			@Probed(UserFinished.class) final EventCurrentSimulationTimeProbe<UserFinished> userFinishedProbe) {

		/*
		 * TODO: Find a way to implement MeasuringPoints into this system. Currently, we don't
		 * pass any measuring points (null).
		 */
		return calculatorFactory.buildCalculator(MetricDescriptionConstants.RESPONSE_TIME_METRIC,
				null,
				DefaultCalculatorProbeSets.createStartStopProbeConfiguration(userStartedProbe, userFinishedProbe));
	}

}
