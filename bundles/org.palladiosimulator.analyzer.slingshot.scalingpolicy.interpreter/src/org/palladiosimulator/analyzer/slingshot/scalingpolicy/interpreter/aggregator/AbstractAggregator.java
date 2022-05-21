package org.palladiosimulator.analyzer.slingshot.scalingpolicy.interpreter.aggregator;

import java.util.Objects;

import javax.measure.quantity.Duration;

import org.jscience.physics.amount.Amount;
import org.palladiosimulator.analyzer.slingshot.monitor.data.SlingshotMeasuringValue;
import org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.TriggerContext;
import org.palladiosimulator.monitorrepository.statisticalcharacterization.ArithmeticMeanAggregator;
import org.palladiosimulator.monitorrepository.statisticalcharacterization.MedianAggregator;
import org.palladiosimulator.monitorrepository.statisticalcharacterization.StatisticalCharacterizationAggregator;

import de.unistuttgart.slingshot.spd.triggers.AGGREGATIONMETHOD;

public abstract class AbstractAggregator {

	private final TriggerContext parent;
	private final AGGREGATIONMETHOD method;
	private final StatisticalCharacterizationAggregator actualAggregator;

	public AbstractAggregator(final TriggerContext parent, final AGGREGATIONMETHOD method) {
		this.parent = Objects.requireNonNull(parent);
		this.method = Objects.requireNonNull(method);
		StatisticalCharacterizationAggregator actualAggregator;

		switch (method) {
		default:
		case AVERAGE:
			actualAggregator = new ArithmeticMeanAggregator(null);
			break;
		case MAX:
			actualAggregator = new MaxAggregator(null);
			break;
		case MEDIAN:
			actualAggregator = new MedianAggregator(null);
			break;
		case MIN:
			actualAggregator = new MinAggregator(null);
			break;
		case SUM:
			actualAggregator = new SumAggregator(null);
			break;
		}

		this.actualAggregator = actualAggregator;
	}

	/**
	 * Returns a non-{@code null} iterable of values to aggregate.
	 * 
	 * @return non-{@code null} iterable.
	 */
	protected abstract Iterable<SlingshotMeasuringValue> dataToAggregate();

	protected abstract Amount<Duration> getIntervallUpperBound();

	protected abstract Amount<Duration> getIntervallLowerBound();

//	public MeasuringValue aggregate() {
//		final Iterable<? extends MeasuringValue> values = this.dataToAggregate();
//		final MeasuringValue result = this.actualAggregator.aggregateData((Iterable<MeasuringValue>) values, 
//				this.getIntervallUpperBound(), this.getIntervallLowerBound(), Optional.empty());
//		
//		
//	}
}
