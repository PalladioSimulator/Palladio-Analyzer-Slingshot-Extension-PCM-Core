package org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations;

/**
 * Specifies different type of cardinalities that is used for contract specification.
 * If an extension method returns no immediate event, both cardinalities are considered
 * as correct.
 * 
 * @author Julijan Katic
 * @see OnEvent
 */
public enum EventCardinality {
	/** Specifies that it returns only a single immediate event. */
	SINGLE,
	/** Specifies that it returns multiple immediate events. */
	MANY
}
