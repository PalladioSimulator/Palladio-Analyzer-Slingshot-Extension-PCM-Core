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

	private static ModelModule INSTANCE = null;

	private /*@ spec_public nullable @*/ Injector injector;
	private /*@ spec_public @*/ final ModelLoader modelLoader;
	private /*@ spec_public @*/ final ModelContainer modelContainer;

	private ModelModule() {
		modelLoader = new ModelLoader();
		modelContainer = new ModelContainer();
	}

	/*@
	  @     requires injector == null;
	  @     assignable injector;
	  @     ensures injector != null;
	  @     ensures \result == injector;
	  @ also
	  @     requires injector != null;
	  @     assignable \nothing;
	  @     ensures \result == injector;
	  @*/
	public Injector getInjector() {
		if (injector == null) {
			modelContainer.addModules(modelLoader.getAllProviders());
			injector = Guice.createInjector(modelContainer.getModelProviders());
		}
		return injector;
	}

	public ModelContainer getModelContainer() {
		return modelContainer;
	}

	/*@ requires true;
	  @ assignable \nothing;
	  @ ensures \result == (injector != null);
	  @*/
	public /*@ pure @*/ boolean isInitialized() {
		return this.injector != null;
	}

	public static ModelModule getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new ModelModule();
		}
		return INSTANCE;
	}
}
