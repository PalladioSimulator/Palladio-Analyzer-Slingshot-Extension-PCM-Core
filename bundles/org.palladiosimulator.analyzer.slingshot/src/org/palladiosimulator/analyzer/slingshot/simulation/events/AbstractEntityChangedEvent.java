package org.palladiosimulator.analyzer.slingshot.simulation.events;

import java.util.UUID;

/**
 * Represents those types of events that describe a change in an predefined
 * entity.
 * 
 * @author Floriment Klinaku
 *
 * @param <T> The entity that whose state has somehow changed.
 */
public abstract class AbstractEntityChangedEvent<T> implements DESEvent {

	private final double delay;
	private double simulationTime;
	private final String id;
	private final T entity;

	public AbstractEntityChangedEvent(final T entity, final double delay) {
		this.delay = delay;
		this.entity = entity;
		this.id = UUID.randomUUID().toString();
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public double getDelay() {
		return delay;
	}

	@Override
	public double time() {
		return simulationTime;
	}

	@Override
	public void setTime(final double time) {
		this.simulationTime = time;
	}

	public T getEntity() {
		return entity;
	}

}