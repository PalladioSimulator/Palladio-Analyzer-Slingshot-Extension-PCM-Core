package org.palladiosimulator.analyzer.slingshot.simulation.events;

/**
 * Specify this interface when an event should be dispatched at a specific point
 * in time.
 * 
 * @author Julijan Katic
 *
 */
public interface ConcreteTimeEvent {

	public double at();

}
