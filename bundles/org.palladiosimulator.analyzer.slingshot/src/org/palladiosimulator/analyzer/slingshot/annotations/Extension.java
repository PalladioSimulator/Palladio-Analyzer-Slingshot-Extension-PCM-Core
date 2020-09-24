package org.palladiosimulator.analyzer.slingshot.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * This defines an extension to a given extension point and generates the
 * corresponding node in the {@code plugin.xml} file.
 * 
 * @author Julijan Katic
 */
@Documented
@Retention(SOURCE)
@Target(TYPE)
public @interface Extension {

	/** The id of the extension point onto which to extend. */
	String extensionPointId();

	/**
	 * The class that implements or extends the {@link ExtensionPoint}-annotated
	 * class. The default value is {@link Object.class}. If the default value is
	 * left, then the class onto which this is annotated will be used as the
	 * implementation instead.
	 */
	Class<?> implementation() default Object.class;

	/**
	 * This defines the corresponding node in the XML file that contains the
	 * information of (or path to) the {@link #implementation()} class. The default
	 * value is {@link ExtensionPoint#DEFAULT_IMPLEMENTATION_NODE}.
	 */
	String extensionPointImplementationNode() default ExtensionPoint.DEFAULT_IMPLEMENTATION_NODE;

}
