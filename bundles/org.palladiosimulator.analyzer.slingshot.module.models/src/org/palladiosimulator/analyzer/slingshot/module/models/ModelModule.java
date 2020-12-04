package org.palladiosimulator.analyzer.slingshot.module.models;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * A singleton class that is responsible for loading the model and creating a
 * corresponding injector.
 * 
 * @author Julijan Katic
 */
public class ModelModule {

	public static final String EXTENSION_POINT_ID = "org.palladiosimulator.analyzer.slingshot.extensionpoint.model";
	public static final String EXECUTABLE_TAG = "baseClass";

	private Injector injector;
	private final ModelLoader modelLoader;
	private final ModelContainer modelContainer;

	public ModelModule() {
		modelLoader = new ModelLoader();
		modelContainer = new ModelContainer();
	}

	/**
	 * Returns the Guice injector with defined modules.
	 * 
	 * @return The injector.
	 */
	public Injector getInjector() {
		if (injector == null) {
			modelContainer.addModules(modelLoader.getAllProviders());
			injector = Guice.createInjector(modelContainer.getModelProviders());
		}
		return injector;
	}

	/**
	 * Returns the container that is holding all the extension instances.
	 * 
	 * @return The container with extension instances.
	 */
	public ModelContainer getModelContainer() {
		return modelContainer;
	}

	public boolean isInitialized() {
		return this.injector != null;
	}

}
