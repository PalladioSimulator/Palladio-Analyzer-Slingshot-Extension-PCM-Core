package org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.graph;

import java.io.IOException;

public interface GraphExporter {

	void export(final EventGraph graph) throws IOException;

}
