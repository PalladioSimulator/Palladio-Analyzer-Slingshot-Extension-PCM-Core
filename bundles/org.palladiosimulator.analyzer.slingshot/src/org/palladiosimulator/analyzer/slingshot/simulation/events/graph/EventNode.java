package org.palladiosimulator.analyzer.slingshot.simulation.events.graph;

import java.util.List;
import java.util.Set;

/**
 * An event node that is used by {@link EventGraph}. Each node holds the
 * information of the event itself, as well as some further informations like
 * whether a violation of the contract has been made.
 * 
 * @param <T> The type that the node contains.
 * 
 * @author Julijan Katic
 */
public interface EventNode<T> {

	/**
	 * The event that has been processed and saved in this node.
	 * 
	 * @return a non-null event.
	 */
	T getEvent();

	/**
	 * Returns the nodes that point to this node. Semantically, this should mean
	 * that the events have caused the event returned by {@link #getEvent()} by some
	 * event method handler, or according to the contracts, will cause this event.
	 * 
	 * @return The event nodes causing the event saved in this node. Should return
	 *         an empty list instead if there is no one.
	 */
	List<EventNode<T>> getIncomingNodes();

	/**
	 * Returns the nodes that are pointed by this node. Semantically, this should
	 * mean that the event returned by {@link #getEvent()} has caused these events
	 * by some event method handler, or according to the contracts, will be caused
	 * by this event.
	 * 
	 * @return The event nodes caused by this event. Should return an empty list
	 *         instead if there is no one.
	 */
	List<EventNode<T>> getOutgoingNodes();

	/**
	 * Returns a list of exceptions that have violated the contract. This is useful
	 * for debugging the software, as a behavior extension (or even the whole
	 * system) will stop if such a violation exists.
	 * 
	 * However, this can still contain exceptions for other things as well.
	 * 
	 * @return set of exceptions that the event has caused or thrown.
	 */
	Set<? extends Exception> getViolations();
}
