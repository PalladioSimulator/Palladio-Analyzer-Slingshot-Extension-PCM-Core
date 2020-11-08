package org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.graph;

public final class EventEdge {

	private final EventNode parentEvent;
	private final EventNode childEvent;

	public EventEdge(final EventNode parent, final EventNode child) {
		this.parentEvent = parent;
		this.childEvent = child;
	}

	public EventNode getParentEvent() {
		return parentEvent;
	}

	public EventNode getChildEvent() {
		return childEvent;
	}

}
