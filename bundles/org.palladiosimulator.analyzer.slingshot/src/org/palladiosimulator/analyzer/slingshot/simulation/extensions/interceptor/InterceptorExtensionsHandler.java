package org.palladiosimulator.analyzer.slingshot.simulation.extensions.interceptor;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.extensionpoint.AbstractExtensionPointHandler;

public class InterceptorExtensionsHandler extends AbstractExtensionPointHandler<Interceptor> {
	
	private static final String EXTENSION_POINT_ID = "";
	private static final String EXTENSION_EXECUTABLE_NAME = "class";
	
	private static final Logger LOGGER = Logger.getLogger(InterceptorExtensionsHandler.class);

	@Override
	public String getExtensionPointId() {
		return EXTENSION_POINT_ID;
	}

	@Override
	public String getExecutableExtensionName() {
		return EXTENSION_EXECUTABLE_NAME;
	}

	@Override
	protected Class<Interceptor> getProvidersClazz() {
		return Interceptor.class;
	}
	
	
	
}
