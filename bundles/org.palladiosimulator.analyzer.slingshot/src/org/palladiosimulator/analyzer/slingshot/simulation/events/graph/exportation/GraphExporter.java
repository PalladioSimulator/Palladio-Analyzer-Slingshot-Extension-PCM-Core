package org.palladiosimulator.analyzer.slingshot.simulation.events.graph.exportation;

import java.io.IOException;

import org.palladiosimulator.analyzer.slingshot.simulation.events.graph.EventGraph;

public interface GraphExporter {

	void export(final EventGraph graph) throws IOException;

}
