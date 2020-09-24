package org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.behavioral;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.behavioral.decorators.DecoratedSimulationBehaviorProvider;
import org.palladiosimulator.analyzer.slingshot.simulation.extension.AbstractExtensionPointHandler;

/**
 * This class evaluates all the extensions that are defined through the behavior
 * extension point.
 * 
 * @author Julijan Katic
 */
public class BehaviorExtensionsHandler extends AbstractExtensionPointHandler<DecoratedSimulationBehaviorProvider> {

	public static final String BEHAVIOR_EXTENSION_POINT_ID = "org.palladiosimulator.analyzer.slingshot.extensionpoint.behavior";

	private static final Logger LOGGER = Logger.getLogger(BehaviorExtensionsHandler.class);

	@Override
	public String getExtensionPointId() {
		return BEHAVIOR_EXTENSION_POINT_ID;
	}

	@Override
	public String getExecutableExtensionName() {
		return "class";
	}

	@Override
	protected Class<? extends DecoratedSimulationBehaviorProvider> getProvidersClazz() {
		return DecoratedSimulationBehaviorProvider.class;
	}

}
