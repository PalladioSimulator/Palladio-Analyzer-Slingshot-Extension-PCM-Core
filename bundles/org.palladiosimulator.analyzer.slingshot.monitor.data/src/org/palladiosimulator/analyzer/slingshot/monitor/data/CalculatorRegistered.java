package org.palladiosimulator.analyzer.slingshot.monitor.data;

import org.palladiosimulator.analyzer.slingshot.simulation.events.AbstractEntityChangedEvent;
import org.palladiosimulator.probeframework.calculator.Calculator;

public final class CalculatorRegistered extends AbstractEntityChangedEvent<Calculator> {

	public CalculatorRegistered(final Calculator entity) {
		super(entity, 0);
	}

}
