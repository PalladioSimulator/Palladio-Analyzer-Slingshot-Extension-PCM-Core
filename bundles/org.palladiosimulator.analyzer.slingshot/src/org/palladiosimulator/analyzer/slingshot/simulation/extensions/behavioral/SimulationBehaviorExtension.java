package org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral;

import org.palladiosimulator.analyzer.slingshot.simulation.extensions.model.SimulationModel;

/**
 * This interface describes a extension for the behaviour extension point and should
 * be used. TODO: Further description and example.
 * 
 * @author Julijan Katic
 */
public interface SimulationBehaviorExtension {
	
	void init(SimulationModel model);
	
}
