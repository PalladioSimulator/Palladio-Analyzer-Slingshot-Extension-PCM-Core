package org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities;

import org.palladiosimulator.analyzer.slingshot.common.utils.stoexproxy.AbstractStoExProxy;
import org.palladiosimulator.pcm.core.PCMRandomVariable;

/**
 * Proxy class responsible to calculate the inter arrival time for a user.
 * 
 * @author Julijan Katic
 * 
 */
public class InterArrivalTime extends AbstractStoExProxy<Double> {

	public InterArrivalTime(final PCMRandomVariable interArrivalRV) {
		super(interArrivalRV, Double.class);
	}

}
