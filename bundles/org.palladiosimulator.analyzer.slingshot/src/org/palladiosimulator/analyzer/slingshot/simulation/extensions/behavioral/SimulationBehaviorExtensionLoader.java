package org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral;

import org.palladiosimulator.analyzer.slingshot.simulation.SlingshotCorePlugin;
import org.palladiosimulator.analyzer.slingshot.util.extensionpoint.AbstractClassExtensionPointHandler;

public class SimulationBehaviorExtensionLoader extends AbstractClassExtensionPointHandler<SimulationBehaviorExtension> {

	@Override
	public String getExtensionPointId() {
		return SlingshotCorePlugin.BEHAVIOR_EXTENSION_POINT_ID;
	}

	@Override
	public String getExecutableExtensionName() {
		return "baseClass";
	}

	@Override
	protected Class<? extends SimulationBehaviorExtension> getProvidersClazz() {
		return SimulationBehaviorExtension.class;
	}

}
