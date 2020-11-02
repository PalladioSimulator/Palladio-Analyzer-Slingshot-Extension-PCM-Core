package org.palladiosimulator.analyzer.slingshot.simulation.events;

import com.google.common.base.Preconditions;

/**
 * Represents those types of events that describe a change in an predefined
 * entity.
 * 
 * @author Floriment Klinaku
 *
 * @param <T> The entity whose state has somehow changed.
 */
public abstract class AbstractEntityChangedEvent<T> implements DESEvent {

	private final double delay;
	private double simulationTime;
	private final String id;
	private final T entity;

	/**
	 * Creates the instance holding the changing entity. The id of this event will
	 * be the combination of the concrete class name that extends this class and the
	 * {@link Object#hashCode()} of the entity.
	 * 
	 * @param entity The non-null entity that somehow changed.
	 * @param delay  A delay of the event.
	 */
	public AbstractEntityChangedEvent(final T entity, final double delay) {
		Preconditions.checkNotNull(entity);

		this.delay = delay;
		this.entity = entity;
		this.id = String.format("%s-%X", this.getClass().getSimpleName(), entity.hashCode());
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

	/**
	 * Returns the entity.
	 * 
	 * @return the entity.
	 */
	public T getEntity() {
		return entity;
	}

}