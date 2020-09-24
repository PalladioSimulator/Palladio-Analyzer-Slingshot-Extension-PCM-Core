package org.palladiosimulator.analyzer.slingshot.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * This annotates the interface or class that is needed for an extension to
 * either be extended from (in case of a class) or implemented (in case of an
 * interface). Furthermore, this will generate a {@code .exsd} file under the
 * {@code schema} directory that registers the extension point in the
 * {@code plugin.xml} file.
 * 
 * @author Julijan Katic
 */
@Retention(SOURCE)
@Target(TYPE)
@Documented
public @interface ExtensionPoint {

	/**
	 * This should give the id of the extension point. This will be used when
	 * creating the schema (.exsd) file and refering it in the plugin.xml. Also,
	 * this will be used in the extensions when refering to the extension point.
	 * This id should be unique within the Eclipse application.
	 * 
	 * @return The id of this extension.
	 */
	public String id();

	/**
	 * Returns a short name of the extension point. This is important for
	 * documentation.
	 * 
	 * @return the name of the extension.
	 */
	public String name();

	/**
	 * @return The non-null, non-empty name of the plugin in which the extension
	 *         point lies.
	 */
	public String pluginName();

	/**
	 * Defines the corresponding node in the {@code .exsd} file that will contain
	 * the information (path) of the implementing class.
	 */
	public String providerNode() default DEFAULT_IMPLEMENTATION_NODE;

	/**
	 * The default value used in {@link #xmlImplementationNode()}.
	 */
	public static final String DEFAULT_IMPLEMENTATION_NODE = "client";
}
