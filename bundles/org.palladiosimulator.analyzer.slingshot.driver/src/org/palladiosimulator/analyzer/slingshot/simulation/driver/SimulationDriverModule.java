package org.palladiosimulator.analyzer.slingshot.simulation.driver;

import org.palladiosimulator.analyzer.slingshot.simulation.api.Simulation;
import org.palladiosimulator.analyzer.slingshot.simulation.api.SimulationScheduling;

import com.google.inject.AbstractModule;

public class SimulationDriverModule extends AbstractModule {

	@Override
	protected void configure() {
		super.configure();

		this.bind(Simulation.class).to(SimulationDriver.class);
		this.bind(SimulationScheduling.class).to(SimulationDriver.class);
	}

}
