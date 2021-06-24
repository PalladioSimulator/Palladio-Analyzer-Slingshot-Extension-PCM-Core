package org.palladiosimulator.analyzer.slingshot.simulation.core;

import org.palladiosimulator.analyzer.slingshot.util.extensionpoint.AbstractExtensionPointHandler;

import com.google.inject.Module;

public final class ModuleLoader extends AbstractExtensionPointHandler<Module> {

	public static final String MODULE_EXTENSION_POINT_ID = "org.palladiosimulator.analyzer.slingshot.module";
	public static final String MODULE_ATTRIBUTE_NAME = "module-definition";

	@Override
	public String getExtensionPointId() {
		return MODULE_EXTENSION_POINT_ID;
	}

	@Override
	public String getExecutableExtensionName() {
		return MODULE_ATTRIBUTE_NAME;
	}

	@Override
	protected Class<? extends Module> getProvidersClazz() {
		return Module.class;
	}

}
