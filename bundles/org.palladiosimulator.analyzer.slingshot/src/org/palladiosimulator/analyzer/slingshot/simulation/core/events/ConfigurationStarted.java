package org.palladiosimulator.analyzer.slingshot.simulation.core.events;

import java.util.Objects;

import org.palladiosimulator.analyzer.slingshot.simulation.events.AbstractEvent;

import de.uka.ipd.sdq.simucomframework.SimuComConfig;

/**
 * This event will be published as soon as the configuration of the simulation
 * system has started. After that, {@link SimulationStarted} will be published.
 * 
 * @author Julijan Katic
 */
public final class ConfigurationStarted extends AbstractEvent {

	private final SimuComConfig simuComConfig;

	public ConfigurationStarted(final SimuComConfig simuComConfig) {
		super(ConfigurationStarted.class, 0);
		this.simuComConfig = Objects.requireNonNull(simuComConfig).getClone();
	}

	public SimuComConfig getConfiguration() {
		return this.simuComConfig;
	}

}
