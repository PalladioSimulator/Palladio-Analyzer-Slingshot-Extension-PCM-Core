package org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.behavioral;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.behavioral.decorators.DecoratedSimulationBehaviorProvider;

/*
 * https://www.vogella.com/tutorials/EclipseExtensionPoint/article.html
 * https://www.eclipse.org/articles/Article-Plug-in-architecture/plugin_architecture.html
 * https://www.sigs-datacom.de/uploads/tx_dmjournals/hennig_seeberger_JS_01_08.pdf
 */
/**
 * This class evaluates all the extensions that are defined through the behavior
 * extension point.
 * 
 * @author Julijan Katic
 */
public class BehaviorExtensionsHandler {

	public static final String BEHAVIOR_EXTENSION_POINT_ID = "org.palladiosimulator.analyzer.slingshot.extensionpoint.behaviour";

	private static final Logger LOGGER = Logger.getLogger(BehaviorExtensionsHandler.class);

	/**
	 * This method will return all providers of every extension that is compatible
	 * to the extension point.
	 * 
	 * @return the list of all providers. If there are no valid extensions or a
	 *         exception occurs, an empty list will be returned.
	 */
	public static List<DecoratedSimulationBehaviorProvider> getAllProviders() {
		final List<DecoratedSimulationBehaviorProvider> providers = new ArrayList<>();

		try {
			final IConfigurationElement[] config = Platform.getExtensionRegistry()
					.getConfigurationElementsFor(BEHAVIOR_EXTENSION_POINT_ID);

			for (final IConfigurationElement e : config) {
				final Object o = e.createExecutableExtension("class");

				if (o instanceof DecoratedSimulationBehaviorProvider) {
					LOGGER.debug("Found DecoratedSimulationBehaviourProvider!");

					final DecoratedSimulationBehaviorProvider provider = (DecoratedSimulationBehaviorProvider) o;
					providers.add(provider);
				}
			}
		} catch (final CoreException e) {
			LOGGER.error("The extension could not be added due to an exception", e);
		}

		return providers;
	}

}
