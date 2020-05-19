package org.palladiosimulator.analyzer.slingshot.simulation.api;

public interface Simulation {

	void init(SimulationModel model) throws Exception;
	
	void startSimulation();
	
}
