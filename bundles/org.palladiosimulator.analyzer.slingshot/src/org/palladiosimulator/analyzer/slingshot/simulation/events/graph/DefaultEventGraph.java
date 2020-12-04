package org.palladiosimulator.analyzer.slingshot.simulation.events.graph;

import java.io.IOException;

import org.palladiosimulator.analyzer.slingshot.simulation.events.graph.exportation.GraphExporter;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;

public class DefaultEventGraph implements EventGraph {

	private final MutableGraph<EventNode<?>> graph;

	public DefaultEventGraph() {
		this.graph = GraphBuilder.directed()
		        .allowsSelfLoops(true)
		        .build();
	}

	@Override
	public void addNode(final EventNode<?> node) {
		this.graph.addNode(node);
	}

	@Override
	public void addEdge(final EventEdge eventEdge) {
		this.graph.addNode(eventEdge.getParentEvent());
		this.graph.addNode(eventEdge.getChildEvent());
		this.graph.putEdge(eventEdge.getParentEvent(), eventEdge.getChildEvent());
	}

	@Override
	public void exportGraph(final GraphExporter exporter) throws IOException {
		exporter.export(this);
	}

	@Override
	public Iterable<EventNode<?>> getNodes() {
		return this.graph.nodes();
	}

	@Override
	public Iterable<EventEdge> getEdges() {
		return this.graph.edges().stream()
		        .map(pair -> new EventEdge(pair.source(), pair.target()))::iterator;
	}

}
