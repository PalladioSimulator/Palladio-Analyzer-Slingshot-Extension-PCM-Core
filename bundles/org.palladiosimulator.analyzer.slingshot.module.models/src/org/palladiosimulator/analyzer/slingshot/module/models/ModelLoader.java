package org.palladiosimulator.analyzer.slingshot.module.models;

import org.palladiosimulator.analyzer.slingshot.util.extensionpoint.AbstractExtensionPointHandler;

final class ModelLoader extends AbstractExtensionPointHandler<ModelProvider> {

	@Override
	public String getExtensionPointId() {
		return ModelModule.EXTENSION_POINT_ID;
	}

	@Override
	public String getExecutableExtensionName() {
		// TODO Auto-generated method stub
		return ModelModule.EXECUTABLE_TAG;
	}

	@Override
	protected Class<? extends ModelProvider> getProvidersClazz() {
		return ModelProvider.class;
	}

}
