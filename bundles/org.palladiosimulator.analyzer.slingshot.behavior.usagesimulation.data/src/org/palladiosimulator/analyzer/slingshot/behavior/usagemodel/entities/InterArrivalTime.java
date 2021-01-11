package org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities;

import org.palladiosimulator.analyzer.slingshot.common.utils.stoexproxy.AbstractStoExProxy;
import org.palladiosimulator.pcm.core.PCMRandomVariable;

public class InterArrivalTime extends AbstractStoExProxy<Double> {

	public InterArrivalTime(PCMRandomVariable interArrivalRV) {
		super(interArrivalRV, Double.class);
	}

}
