package org.palladiosimulator.analyzer.slingshot.monitor;

import org.palladiosimulator.analyzer.slingshot.simulation.api.SimulationMonitoring;

import com.google.inject.AbstractModule;

public class MonitorModule extends AbstractModule {

	@Override
	protected void configure() {
		super.configure();
		this.bind(SimulationMonitoring.class).to(Monitoring.class);
		this.bind(MonitoringBehavior.class);
	}

}
