package org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.behavioural.results;

import java.util.Set;

import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;

/**
 * This specialized class represents an empty result event that does not contain any
 * subsequent events. It is mainly used for compile-time checks of the contract system
 * as it eases the process.
 * 
 * @author Julijan Katic
 */
public class EmptyResultEvent extends ResultEvent<DESEvent>{

	/**
	 * Creates an empty result event. However, instead of using the
	 * constructor, the {@link ResulEvent#empty()} should be used instead.
	 */
	public EmptyResultEvent() {
		super(Set.of());
	}

}
