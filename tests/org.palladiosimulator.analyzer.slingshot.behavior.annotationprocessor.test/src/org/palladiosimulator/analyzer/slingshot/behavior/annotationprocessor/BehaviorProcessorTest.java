package org.palladiosimulator.analyzer.slingshot.behavior.annotationprocessor;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.tools.Diagnostic.Kind;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.palladiosimulator.analyzer.slingshot.annotationprocessor.test.utils.AbstractAnnotationTester;

public class BehaviorProcessorTest extends AbstractAnnotationTester {

	private final String sourceDirectory = "resource/";

	@Before
	public void init() throws IOException {
		this.compiler = ToolProvider.getSystemJavaCompiler();
	}

	private DiagnosticCollector<JavaFileObject> commonTestSetup(final List<String> additionalFiles,
	        final String graphExport) throws IOException {
		final List<String> files = new ArrayList<>(
		        List.of("resource/events/SampleEventA.java", "resource/events/SampleEventB.java"));
		files.addAll(additionalFiles);

		final Iterable<String> options;
		if (graphExport == null || graphExport.isBlank()) {
			options = Set.of("-Anoexport=true");
		} else {
			options = Set.of("-Aexport=" + graphExport.strip());
		}

		final DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
		final CompilationTask compilationTask = compiler.getTask(new PrintWriter(System.out), null, diagnostics,
		        options, null, getSourceFiles(files));

		compilationTask.setProcessors(List.of(new OnEventProcessor()));
		compilationTask.call();

		diagnostics.getDiagnostics().stream()
		        .forEach(System.out::println);

		return diagnostics;
	}

	@Test
	public void testWorkingExample() throws IOException {

		final DiagnosticCollector<JavaFileObject> diagnostics = commonTestSetup(
		        List.of("resource/ExampleContractChecker.java"), "EventWorkingExampleGraph.dot");

		Assert.assertTrue(diagnostics.getDiagnostics().stream()
		        .filter(diagnostic -> diagnostic.getKind() == Kind.ERROR)
		        .count() == 0);

	}

	@Test
	public void testMissedContract() throws IOException {

		final DiagnosticCollector<JavaFileObject> diagnostics = commonTestSetup(
		        List.of("resource/ExampleMissedContract.java"), null);

		Assert.assertTrue(diagnostics.getDiagnostics().size() > 0);
	}

	@Test
	public void testWrongParameter() throws IOException {

		final DiagnosticCollector<JavaFileObject> diagnostics = commonTestSetup(
		        List.of("resource/ExampleWrongParameter.java"), null);

		Assert.assertTrue(diagnostics.getDiagnostics().size() > 0); // TODO: Better Testing for diagnostics.
	}

	@Test
	public void testWhenEventIsDESEvent() throws IOException {
		final DiagnosticCollector<JavaFileObject> diagnostics = commonTestSetup(
		        List.of("resource/WhenEventIsDESEvent.java"), null);

		Assert.assertTrue(diagnostics.getDiagnostics().size() > 0);
	}

	@Test
	public void testThenEventHasDESEvent() throws IOException {
		final DiagnosticCollector<JavaFileObject> diagnostics = commonTestSetup(
		        List.of("resource/ThenEventHasDESEvent.java"), null);

		Assert.assertTrue(diagnostics.getDiagnostics().size() > 0);
	}
}
