package org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral;

import org.palladiosimulator.analyzer.slingshot.simulation.extensions.model.SimulationModel;

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

	/**
	 * Initializes the extension using a simulation model. Will be called first
	 * 
	 * @param model The model of the simulation that was configured at start.
	 * @deprecated SimulationModel is not considered to be used anymore. Instead,
	 *             injectable constructors should be used instead.
	 */
	@Deprecated
	default void init(final SimulationModel model) {
		this.init();
	}
}
