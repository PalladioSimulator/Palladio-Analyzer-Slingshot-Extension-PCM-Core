package org.palladiosimulator.analyzer.slingshot.simulation.utils;

import java.util.HashSet;

import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;

/**
 * This class resembles a Set that is used to further enforce the contract
 * specification. Unlike a normal set, this class also provides which events
 * are allowed to be saved and hence assures the user that only the provided
 * events are set.
 * 
 * @author Julijan Katic
 */
public class DESEventSet extends HashSet<DESEvent> {

	private static final long serialVersionUID = -7024455657417874583L;

	public void addEvent(final DESEvent event) {
		this.add(event);
	}
	
}
