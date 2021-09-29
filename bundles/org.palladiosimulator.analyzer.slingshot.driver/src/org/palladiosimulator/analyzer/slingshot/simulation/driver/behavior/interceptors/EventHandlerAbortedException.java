package org.palladiosimulator.analyzer.slingshot.simulation.driver.behavior.interceptors;

public final class EventHandlerAbortedException extends RuntimeException {

	public EventHandlerAbortedException(final String message) {
		super(message);
	}

}
