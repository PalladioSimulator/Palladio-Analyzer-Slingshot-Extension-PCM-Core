package org.palladiosimulator.analyzer.slingshot.extensionpoint.annotationprocessor.test;

import java.io.File;
import java.io.PrintWriter;
import java.util.Arrays;

import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.palladiosimulator.analyzer.slingshot.annotationprocessor.test.utils.AbstractAnnotationTester;
import org.palladiosimulator.analyzer.slingshot.extensionpoint.annotationprocessor.extension.ExtensionProcessor;
import org.w3c.dom.Node;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.builder.Input;
import org.xmlunit.diff.DefaultNodeMatcher;
import org.xmlunit.diff.Diff;
import org.xmlunit.diff.ElementSelectors;

/**
 * Test class for testing the annotation processor {@link ExtensionProcessor}
 * for the .
 * 
 * @author Julijan Katic
 */
public class ExtensionTest extends AbstractAnnotationTester {

	private final String sourceDirectory = "resource/test01";

	private final String samplePluginXMLFile = "resource/test01/samplePlugin.xml";
	private final String expectedPluginXMLFile = "resource/test01/expectedPlugin.xml";

	private Iterable<JavaFileObject> simpleExtensionClass;

	@Before
	public void configureCompilation() throws Exception {
		this.compiler = ToolProvider.getSystemJavaCompiler();
		this.simpleExtensionClass = getSourceFiles(sourceDirectory);
	}

	@Test
	public void runAnnotationProcessorForSimpleClassAndCheckXMLFiles() throws Exception {
		final CompilationTask compilationTask = compiler.getTask(new PrintWriter(System.out), null, null, null, null,
		        simpleExtensionClass);

		compilationTask.setProcessors(Arrays.asList(new ExtensionProcessor(samplePluginXMLFile)));
		compilationTask.call();

		final Diff differencesInXML = DiffBuilder.compare(Input.fromFile(samplePluginXMLFile))
		        .ignoreComments()
		        .withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byName))
		        /*
		         * Only the correct nodes are important (not their order and no whitespaces
		         * etc.)
		         */
		        .checkForSimilar()
		        /* Ignore Processing Instructions "<? ... ?>" */
		        .withNodeFilter(node -> node.getNodeType() != Node.PROCESSING_INSTRUCTION_NODE)
		        .withTest(Input.fromFile(expectedPluginXMLFile))
		        .build();

		Assert.assertFalse(differencesInXML.toString(), differencesInXML.hasDifferences());
	}

	@After
	public void deleteCompiledXMLFile() {
		final File xmlFile = new File(samplePluginXMLFile);
		if (xmlFile.exists()) {
			xmlFile.delete();
		}
	}

}
