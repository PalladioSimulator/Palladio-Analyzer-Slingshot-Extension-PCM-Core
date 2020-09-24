package org.palladiosimulator.analyzer.slingshot.extensionpoint.annotationprocessor.test;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.tools.StandardLocation;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.wiring.BundleWiring;
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
	@SuppressWarnings("deprecation")
	public void createCompilationWithAnnotationProcessor() {
		sampleExtensionUrl = ExtensionTest.class.getResource("/resources/SampleExtension.java");

		final Compiler compiler = Compiler.javac();
		final ClassLoader loader = Activator.getContext().getBundle().adapt(BundleWiring.class).getClassLoader();

		compilation = compiler.withProcessors(new ExtensionProcessor()).withClasspathFrom(loader)
				.compile(JavaFileObjects.forResource(sampleExtensionUrl));
	}

	@Test
	public void testGeneration() {
		CompilationSubject.assertThat(compilation).generatedFile(StandardLocation.SOURCE_OUTPUT, "plugin.xml");
	}

	// @Test
	public void sampleTest() {

		// getClasspathFromClassloader(loader);

		Assert.assertTrue(true);
	}

	public static void getClasspathFromClassloader(ClassLoader currentClassLoader) {
		final ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
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
			} else {
				throw new IllegalArgumentException(String.format(
						"Classpath for compilation could not be extracted since %s is not an instance of URLClassloader",
						currentClassLoader));
			}

			currentClassLoader = currentClassLoader.getParent();
		}

		System.out.println("Show classPath:");
		classPaths.forEach(System.out::println);
	}

}
