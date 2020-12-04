package org.palladiosimulator.analyzer.slingshot.simulation.core.events;

/**
 * An event that notifies the system to stop immediately due to an error.
 * 
 * @author Julijan Katic
 */
public class SimulationInterrupted extends SimulationFinished {

	private final Throwable error;

	public SimulationInterrupted(final Throwable error) {
		this.error = error;
	}

	public SimulationInterrupted(final String errorMessage) {
		this.error = new RuntimeException(errorMessage);
	}

	public Throwable getError() {
		return error;
	}
}
