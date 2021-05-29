package org.palladiosimulator.analyzer.slingshot.monitor;

import org.palladiosimulator.analyzer.slingshot.simulation.core.SimulationMonitoring;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class MonitorModule extends AbstractModule {

	@Provides
	public SimulationMonitoring createMonitoring() {
		return new Monitoring();
	}

}
