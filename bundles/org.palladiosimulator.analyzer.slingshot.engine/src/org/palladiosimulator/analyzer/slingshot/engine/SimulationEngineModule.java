package org.palladiosimulator.analyzer.slingshot.engine;

import org.palladiosimulator.analyzer.slingshot.engine.ssj.SimulationEngineSSJ;
import org.palladiosimulator.analyzer.slingshot.simulation.api.SimulationEngine;

import com.google.inject.AbstractModule;

public final class SimulationEngineModule extends AbstractModule {
	
	@Override
	protected void configure() {
		super.configure();
		bind(SimulationEngine.class).to(SimulationEngineSSJ.class);
	}
	
}
