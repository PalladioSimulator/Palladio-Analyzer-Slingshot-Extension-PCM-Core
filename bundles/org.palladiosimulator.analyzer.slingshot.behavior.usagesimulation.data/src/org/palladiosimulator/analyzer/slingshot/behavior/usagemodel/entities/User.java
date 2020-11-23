package org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities;

import org.palladiosimulator.analyzer.slingshot.simulation.core.entities.IUser;

import de.uka.ipd.sdq.simucomframework.variables.stackframe.SimulatedStack;

/**
 * An entity that represents a user within the usage model. In this simulator,
 * each user has an own simulation stack for variables.
 * 
 * @author Julijan Katic
 */
public class User implements IUser {

	private final SimulatedStack<Object> stack;

	public User() {
		this.stack = new SimulatedStack<>();
	}

	public SimulatedStack<Object> getStack() {
		return this.stack;
	}
}
