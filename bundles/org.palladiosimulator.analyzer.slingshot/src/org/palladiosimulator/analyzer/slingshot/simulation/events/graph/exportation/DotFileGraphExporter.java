package org.palladiosimulator.analyzer.slingshot.simulation.events.graph.exportation;

import java.io.IOException;
import java.io.Writer;

import org.palladiosimulator.analyzer.slingshot.simulation.events.graph.EventEdge;
import org.palladiosimulator.analyzer.slingshot.simulation.events.graph.EventGraph;

public class DotFileGraphExporter implements GraphExporter {

	private final Writer writer;

	public DotFileGraphExporter(final Writer writer) {
		this.writer = writer;
	}

	@Override
	public void export(final EventGraph graph) throws IOException {
		final StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append("strict digraph EventGraph {\n");

		for (final EventEdge edge : graph.getEdges()) {
			final String edgeString = String.format("\t%s -> %s;\n", edge.getParentEvent(), edge.getChildEvent());
			stringBuilder.append(edgeString);
		}

		stringBuilder.append("}");
		writer.write(stringBuilder.toString());
		writer.flush();
		writer.close();
	}

}
