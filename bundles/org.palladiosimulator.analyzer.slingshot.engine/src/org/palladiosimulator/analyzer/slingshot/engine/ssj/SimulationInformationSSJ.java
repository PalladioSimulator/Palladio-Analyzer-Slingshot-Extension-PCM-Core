package org.palladiosimulator.analyzer.slingshot.engine.ssj;

import org.palladiosimulator.analyzer.slingshot.simulation.core.entities.SimulationInformation;

import umontreal.ssj.simevents.Simulator;

final class SimulationInformationSSJ implements SimulationInformation {
	
	private final Simulator simulator;
	private int processedEvents = 0;
	
	SimulationInformationSSJ(final Simulator simulator) {
		this.simulator = simulator;
	}

	@Override
	public double currentSimulationTime() {
		return simulator.time();
	}

	@Override
	public int currentNumberOfProcessedEvents() {
		return processedEvents;// simulator.
	}
	
	@Override
	public int compareTo(final SimulationInformation simulationInformation) {
		return Double.compare(this.currentSimulationTime(), simulationInformation.currentSimulationTime());
	}

	void setNumberOfProcessedEvents(int processedEvents) {
		this.processedEvents = processedEvents;
	}
	
	void increaseNumberOfProcessedEvents() {
		this.processedEvents++;
	}
}
