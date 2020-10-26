package org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.graph;

public final class EventEdge {

	private final String parentEvent;
	private final String childEvent;

	public EventEdge(final String parent, final String child) {
		this.parentEvent = parent;
		this.childEvent = child;
	}

	public String getParentEvent() {
		return parentEvent;
	}

	public String getChildEvent() {
		return childEvent;
	}

}
