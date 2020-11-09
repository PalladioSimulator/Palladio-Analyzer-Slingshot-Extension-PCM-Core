package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.resources.passive;

/**
 * This interface represents a single passive resource. A passive resource is
 * typically a logical resource such as Databases, Threads, etc. Indifferent to
 * active resources, a passive resource can be acquired and released afterwards.
 * 
 * 
 * @author Julijan Katic
 */
public interface IPassiveResource {

	/**
	 * Acquires the resources if possible. If it couldn't be acquired, then
	 * {@code false} is returned.
	 * 
	 * @param numberOfInstances the number of units to acquire.
	 * @return true iff the resource is acquired.
	 */
	boolean acquire(int numberOfInstances);

	/**
	 * Releases the resource again for others to use.
	 * 
	 * @param numberOfInstances the number of units to release.
	 */
	void release(int numberOfInstances);

	/**
	 * Returns the maximal number of instances that can be acquired at the same
	 * time.
	 * 
	 * @return max. number of instances acquirable at the same time.
	 */
	int getCapacity();

}
