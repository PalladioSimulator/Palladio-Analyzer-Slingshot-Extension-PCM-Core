package org.palladiosimulator.analyzer.slingshot.simulation.api;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.e4.core.di.annotations.Execute;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.behavioural.decorators.AbstractDecoratedSimulationBehaviorProvider;

/*
 * Following the tutorial on https://www.vogella.com/tutorials/EclipseExtensionPoints/article.html
 */

/**
 * Evaluates all extensions to the system. As of now, it only handles the behavior extensions.
 * 
 * @author Julijan Katic
 */
public class EvaluateContributionHandler {

	private static final String BEHAVIOUR_ID = "org.palladiosimulator.analyzer.slingshot.extensionpoint.behaviour";
	
	private static final Logger LOGGER = Logger.getLogger(EvaluateContributionHandler.class);
	
	@Execute
	public void execute(final IExtensionRegistry registry) {
		final IConfigurationElement[] configs = registry.getConfigurationElementsFor(BEHAVIOUR_ID);
		
		try {
			for (final IConfigurationElement config : configs) {
				LOGGER.info("Evaluating extensions");
				final Object o = config.createExecutableExtension("class"); // This "class" refers to the XML child element 'class' defined in the schema file
				if (o instanceof AbstractDecoratedSimulationBehaviorProvider) {
					executeExtension((AbstractDecoratedSimulationBehaviorProvider) o);
				}
			}
		} catch (final CoreException ex) {
			LOGGER.error("evaluation extensions", ex);
		}
	}
	
	/**
	 * Helper method to register a provider into the SimulationFactory
	 * @param provider
	 */
	private void executeExtension(final AbstractDecoratedSimulationBehaviorProvider provider) {
		final ISafeRunnable runnable = new ISafeRunnable() {
			
			@Override
			public void handleException(final Throwable e) {
				LOGGER.error("error while executing extension", e);
			}
			
			@Override
			public void run() throws Exception {
				
			}
			
		};
		
		SafeRunner.run(runnable);
	}
}
