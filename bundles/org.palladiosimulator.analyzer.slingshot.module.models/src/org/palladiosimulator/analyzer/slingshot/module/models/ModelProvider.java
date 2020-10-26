package org.palladiosimulator.analyzer.slingshot.module.models;

import com.google.inject.Module;

/**
 * A module provider gives a possibility to provide a new model to the
 * simulation system. Each ModelProvider is also just a {@link Module} in the
 * sense of Google Guice.
 * 
 * @author Julijan Katic
 */
public interface ModelProvider extends Module {

}
