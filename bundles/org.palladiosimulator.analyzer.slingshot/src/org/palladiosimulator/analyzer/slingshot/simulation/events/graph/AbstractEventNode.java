package org.palladiosimulator.analyzer.slingshot.simulation.events.graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class AbstractEventNode<T> implements EventNode<T> {

	private final T event;
	private final List<EventNode<T>> incomingNodes;
	private final List<EventNode<T>> outgoingNodes;
	private final Set<? extends Exception> violations;

	public AbstractEventNode(final T event) {
		this.event = event;
		this.incomingNodes = new ArrayList<>();
		this.outgoingNodes = new ArrayList<>();
		this.violations = new HashSet<>();
	}

	@Override
	public T getEvent() {
		return event;
	}

	@Override
	public List<EventNode<T>> getIncomingNodes() {
		return incomingNodes;
	}

	@Override
	public List<EventNode<T>> getOutgoingNodes() {
		return outgoingNodes;
	}

	@Override
	public Set<? extends Exception> getViolations() {
		return violations;
	}

}
