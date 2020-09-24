package org.palladiosimulator.analyzer.slingshot.extensionpoint.annotationprocessor;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.tools.StandardLocation;

import org.junit.Before;
import org.junit.Test;
import org.palladiosimulator.analyzer.slingshot.extensionpoint.annotationprocessor.extension.ExtensionProcessor;

import com.google.common.base.Splitter;
import com.google.common.base.StandardSystemProperty;
import com.google.common.collect.Iterables;
import com.google.testing.compile.Compilation;
import com.google.testing.compile.CompilationSubject;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;

public class ExtensionTest {

	private Compilation compilation;
	private URL sampleExtensionUrl;

	@Before
	public void createCompilationWithAnnotationProcessor() {
		getClasspathFromClassloader();
		sampleExtensionUrl = ExtensionTest.class.getResource("/resources/SampleExtension.java");

		final Compiler compiler = Compiler.javac();

		compilation = compiler.withProcessors(new ExtensionProcessor())
				.compile(JavaFileObjects.forResource(sampleExtensionUrl));
	}

	@Test
	public void testGeneration() {
		CompilationSubject.assertThat(compilation).generatedFile(StandardLocation.SOURCE_OUTPUT, "plugin.xml");
	}

	private static void getClasspathFromClassloader() {
		final ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
		ClassLoader currentClassLoader = systemClassLoader;

		final Set<String> classPaths = new LinkedHashSet<>();

		while (true) {
			if (currentClassLoader == systemClassLoader) {
				Iterables.addAll(classPaths, Splitter.on(StandardSystemProperty.PATH_SEPARATOR.value())
						.split(StandardSystemProperty.JAVA_CLASS_PATH.value()));
				break;
			}
			if (currentClassLoader instanceof URLClassLoader) {
				for (final URL url : ((URLClassLoader) currentClassLoader).getURLs()) {
					if (url.getProtocol().equals("file")) {
						classPaths.add(url.getPath());
					}
				}
			}

			currentClassLoader = currentClassLoader.getParent();
		}

		System.out.println("Show classPath:");
		classPaths.forEach(System.out::println);
	}
}
