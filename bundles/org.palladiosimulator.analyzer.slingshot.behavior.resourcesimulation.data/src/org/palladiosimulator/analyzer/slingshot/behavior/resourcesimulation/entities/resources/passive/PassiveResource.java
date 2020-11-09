package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.resources.passive;

import java.util.concurrent.Semaphore;

import com.google.common.base.Preconditions;

/**
 * A passive resource implementation using a semaphore to control the
 * acquisition of this resource.
 * 
 * @author Julijan Katic
 */
public class PassiveResource implements IPassiveResource {

	/**
	 * @see #getCapacity()
	 */
	private final int capacity;

	/**
	 * Used for controlling how many of such acquisitions can be used at the same
	 * time.
	 */
	private final Semaphore semaphore;

	/**
	 * Instantiates a passive resource.
	 * 
	 * @param capacity Number of instances allowed to acquire at the same time. Must
	 *                 be non-negative.
	 */
	public PassiveResource(final int capacity) {
		Preconditions.checkArgument(capacity >= 0, "capacity must be non-negative");
		this.capacity = capacity;
		this.semaphore = new Semaphore(capacity);
	}

	@Override
	public boolean acquire(final int numberOfInstances) {
		return semaphore.tryAcquire(numberOfInstances);
	}

	@Override
	public void release(final int numberOfInstances) {
		semaphore.release(numberOfInstances);
	}

	@Override
	public int getCapacity() {
		return capacity;
	}

}
