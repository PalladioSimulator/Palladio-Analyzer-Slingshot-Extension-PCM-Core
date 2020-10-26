package org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.graph;

import java.io.IOException;

public interface EventGraph {

	public void addNode(final String node);

	public void addEdge(final EventEdge eventEdge);

	public Iterable<String> getNodes();

	public Iterable<EventEdge> getEdges();

	public void exportGraph(final GraphExporter exporter) throws IOException;
}
