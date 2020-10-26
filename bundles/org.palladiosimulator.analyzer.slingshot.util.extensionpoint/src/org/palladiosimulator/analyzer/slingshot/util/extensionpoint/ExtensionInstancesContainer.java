package org.palladiosimulator.analyzer.slingshot.util.extensionpoint;

import com.google.inject.Injector;
import java.util.List;

/**
 * This interface is a container especially for extension points.
 * 
 * @author Julijan Katic
 */
public interface ExtensionInstancesContainer<ProviderType> {

	/**
	 * Loads all the extensions from a certain provider;
	 */
	void loadExtensions(final Injector injector);

	List<ProviderType> getExtensions();

}
