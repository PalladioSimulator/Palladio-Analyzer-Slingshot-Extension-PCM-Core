package org.palladiosimulator.analyzer.slingshot.simulation.events;

import java.util.List;
import java.util.UUID;


/**
 * 
 * @author Floriment Klinaku
 *
 * @param <T> The typed entity
 */
public abstract class AbstractEntityChangedEvent<T> implements DESEvent{

	protected double delay;
	private double simulationTime;
	// id here not needed
	protected String id;
	private T entity;

	public AbstractEntityChangedEvent(T entity, double delay) {
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
		// TODO Auto-generated method stub
		return simulationTime;
	}

	@Override
	public void setTime(double time) {
		this.simulationTime = time;
	}

	@Override
	public List<DESEvent> handle() {
		// TODO Auto-generated method stub
		return null;
	}

	public T getEntity() {
		return entity;
	}

}