package org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.graph;

import java.io.IOException;
import java.io.Writer;

import javax.annotation.processing.Filer;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

public class DotFileGraphExporter implements GraphExporter {

	private final Writer writer;

	public DotFileGraphExporter(final Filer filer) throws IOException {
		final FileObject fileObject = filer.createResource(StandardLocation.SOURCE_PATH, "", "EventGraph.dot");
		this.writer = fileObject.openWriter();
	}

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
