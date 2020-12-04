package org.palladiosimulator.analyzer.slingshot.simulation.events.graph;

import java.io.IOException;

import org.palladiosimulator.analyzer.slingshot.simulation.events.graph.exportation.GraphExporter;

/**
 * This interface holds information of the event graph. EventGraphs can be used
 * to analyze the events that have been processed or published.
 * 
 * @author Julijan Katic
 * @see EventNode
 */
public interface EventGraph {

	/**
	 * Adds a node to the graph without connections. If the node is already in the
	 * graph, then nothing should happen.
	 * 
	 * @param node The node to add.
	 */
	public void addNode(final EventNode<?> node);

	/**
	 * Adds an edge to the graph. If the nodes aren't in the graph yet, then the
	 * nodes should be added automatically.
	 * 
	 * @param eventEdge The edge to add.
	 */
	public void addEdge(final EventEdge eventEdge);

	/**
	 * Returns an iterable object that can iterate through the nodes in the graph.
	 * 
	 * @return Non-null iterable of event nodes.
	 */
	public Iterable<EventNode<?>> getNodes();

	/**
	 * Returns an iterable object that can iterate through the edges in the graph.
	 * 
	 * @return Non-null iterable of edges.
	 */
	public Iterable<EventEdge> getEdges();

	public void exportGraph(final GraphExporter exporter) throws IOException;
}
