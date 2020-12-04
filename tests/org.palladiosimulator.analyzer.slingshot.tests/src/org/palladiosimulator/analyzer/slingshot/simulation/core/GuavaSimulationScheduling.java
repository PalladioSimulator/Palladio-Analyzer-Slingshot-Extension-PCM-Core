package org.palladiosimulator.analyzer.slingshot.simulation.core;

import java.util.List;

import org.palladiosimulator.analyzer.slingshot.simulation.core.exceptions.EventContractException;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;

import com.google.common.eventbus.EventBus;

public class GuavaSimulationScheduling implements SimulationScheduling {

	private final EventBus eventBus;

	public GuavaSimulationScheduling() {
		this.eventBus = new EventBus();
	}

	@Override
	public void scheduleForSimulation(final DESEvent event) {
		this.eventBus.post(event);
	}

	@Override
	public void scheduleForSimulation(final List<DESEvent> events) {
		events.forEach(this::scheduleForSimulation);
	}

	@Override
	public void checkEventContract(final DESEvent event) throws EventContractException {

	}

	public EventBus getEventBus() {
		return this.eventBus;
	}
}
