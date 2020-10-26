package org.palladiosimulator.analyzer.slingshot.annotationprocessor.test.utils;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

/**
 * This is a general test component.
 * 
 * @author Julijan Katic
 */
public abstract class AbstractAnnotationTester {

	/** The compiler with the annotation processor. */
	protected JavaCompiler compiler;
	/** The compiling task that includes the annotation processor. */
	protected CompilationTask compilationTask;

	/**
	 * Helper method for getting all the source files in a certain directory.
	 * 
	 * @param directory The directory to look after source files.
	 * @return an iterable object that contains {@link JavaFileObjects} for each
	 *         source file.
	 * @throws IOException if an IO operation failed: see
	 *                     {@link StandardJavaFileManager#setLocation(Location, Iterable)}
	 *                     and
	 *                     {@link StandardJavaFileManager#list(Location, String, Set, boolean)}.
	 * 
	 */
	protected Iterable<JavaFileObject> getSourceFiles(final String directory) throws IOException {
		final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		final StandardJavaFileManager files = compiler.getStandardFileManager(null, null, null);

		files.setLocation(StandardLocation.SOURCE_PATH, List.of(new File(directory)));

		final Set<Kind> fileKinds = Collections.singleton(Kind.SOURCE);
		return files.list(StandardLocation.SOURCE_PATH, "", fileKinds, true);
	}

	protected Iterable<? extends JavaFileObject> getSourceFiles(final List<String> files) throws IOException {
		final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		final StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

		return fileManager.getJavaFileObjectsFromStrings(files);
	}
}
