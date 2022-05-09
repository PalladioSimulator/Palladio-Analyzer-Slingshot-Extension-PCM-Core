package org.palladiosimulator.analyzer.slingshot.engine;

import org.palladiosimulator.analyzer.slingshot.engine.ssj.SimulationEngineSSJ;
import org.palladiosimulator.analyzer.slingshot.simulation.api.SimulationEngine;
import org.palladiosimulator.analyzer.slingshot.simulation.core.entities.SimulationInformation;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public final class SimulationEngineModule extends AbstractModule {
	
	@Override
	protected void configure() {
		super.configure();
		bind(SimulationEngine.class).to(SimulationEngineSSJ.class);
	}
	
	@Provides
	public SimulationInformation getSimulationInformation(final SimulationEngine engine) {
		return engine.getSimulationInformation();
	}
	
}
