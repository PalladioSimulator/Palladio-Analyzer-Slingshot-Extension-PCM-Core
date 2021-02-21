package org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities;

import java.util.UUID;

import org.palladiosimulator.analyzer.slingshot.simulation.core.entities.IUser;

import de.uka.ipd.sdq.simucomframework.variables.stackframe.SimulatedStack;

/**
 * An entity that represents a user within the usage model. In this simulator,
 * each user has an own simulation stack for variables.
 * 
 * @author Julijan Katic
 */
public class User implements IUser {

	/** The id of the user. */
	private final String id;

	/** The simulated stack of objects belonging to the simulated user. */
	private final SimulatedStack<Object> stack;

	/**
	 * Constructs the user by creating a new SimulatedStack.
	 */
	public User() {
		this.id = UUID.randomUUID().toString();
		this.stack = new SimulatedStack<>();
	}

	/**
	 * Returns the SimulatedStack from this user.
	 * 
	 * @return the simulated stack.
	 */
	public SimulatedStack<Object> getStack() {
		return this.stack;
	}

	/**
	 * Returns the id of this user.
	 * 
	 * @return the id.
	 */
	public String getId() {
		return this.id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final User other = (User) obj;
		if (this.id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!this.id.equals(other.id)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "User [id=" + this.id + "]";
	}

}
