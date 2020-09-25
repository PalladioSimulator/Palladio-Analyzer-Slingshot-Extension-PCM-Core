package org.palladiosimulator.analyzer.slingshot.simulation.extensions.extensionpoint;

import java.util.List;

/**
 * This interface describes a handler for extension points. Every extension
 * point can be derived using Eclipse's extension point mechanism.
 * 
 * @author Julijan Katic
 */
public interface ExtensionPointHandler<Provider> {

	/**
	 * Returns the extension point id as defined in the plugin.xml.
	 * 
	 * @return the extension point id.
	 */
	public String getExtensionPointId();

	/**
	 * Returns all the providers of every extension provided that is compatible to
	 * the extension point given in plugin.xml. If no extension is provided, an
	 * empty list will be returned.
	 * 
	 * @return list of all extension providers. Will be empty if no extension
	 *         provider was given.
	 */
	public List<Provider> getAllProviders();
	
	/**
	 * An ExtensionPoint as defined in Slingshot always has a child that is referring to a 
	 * class that implements the interface.
	 * 
	 * @return The child node of the extension point referring to the class implementation.
	 */
	public String getExecutableExtensionName();
}
