package org.palladiosimulator.analyzer.slingshot.extensionpoint.annotationprocessor;

import static com.google.testing.compile.Compiler.javac;

import javax.tools.StandardLocation;

import org.junit.Before;
import org.junit.Test;
import org.palladiosimulator.analyzer.slingshot.extensionpoint.annotationprocessor.extension.ExtensionProcessor;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.CompilationSubject;
import com.google.testing.compile.JavaFileObjects;

public class ExtensionTest {

	private Compilation compilation;

	@Before
	public void createCompilationWithAnnotationProcessor() {
		compilation = javac().withProcessors(new ExtensionProcessor())
				.compile(JavaFileObjects.forResource("SampleExtension.java"));
	}

	@Test
	public void testGeneration() {
		CompilationSubject.assertThat(compilation).generatedFile(StandardLocation.SOURCE_OUTPUT, "plugin.xml");
	}
}
