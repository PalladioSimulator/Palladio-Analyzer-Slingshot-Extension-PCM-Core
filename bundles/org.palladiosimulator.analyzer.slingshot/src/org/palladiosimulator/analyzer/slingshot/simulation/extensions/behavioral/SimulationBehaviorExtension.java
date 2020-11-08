package org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral;

/**
 * This interface describes a extension for the behaviour extension point and
 * should be used.
 * 
 * @author Julijan Katic
 */
public interface SimulationBehaviorExtension {

	/**
	 * Initializes the simulation behavior extension. This method will be called
	 * first before any event handler is called.
	 */
	default void init() {
	}

}
