package org.palladiosimulator.analyzer.slingshot.simulation.events.graph;

/**
 * This class describes the directed edge from a parent event to a child event.
 * 
 * @author Julijan Katic
 */
public final class EventEdge {

	private final EventNode<?> parentEvent;
	private final EventNode<?> childEvent;

	/**
	 * Instantiates the EventEdge with the given attributes. Notice that the type
	 * parameters in the referenced EventNodes are not known.
	 * 
	 * @param parent The parent event node from which the edge starts.
	 * @param child  The child event node where the endge ends.
	 */
	public EventEdge(final EventNode<?> parent, final EventNode<?> child) {
		this.parentEvent = parent;
		this.childEvent = child;
	}

	/**
	 * Returns the parent event node of this directed edge, i.e., where the edge
	 * starts. Notice that the type of the node referenced is not known.
	 * 
	 * @return The parent event node.
	 */
	public EventNode<?> getParentEvent() {
		return parentEvent;
	}

	/**
	 * Returns the child event node of this directed edge, i.e., where the edge
	 * ends. Notice that the type of the node referenced is not known.
	 * 
	 * @return The child event node.
	 */
	public EventNode<?> getChildEvent() {
		return childEvent;
	}

}
