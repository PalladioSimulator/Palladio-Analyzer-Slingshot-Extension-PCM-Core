package org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.exceptions;

public class NoContractDefinitionException extends RuntimeException {

	public NoContractDefinitionException(final String methodName) {
		super("Extension Method does not provide a contract definition: " + methodName);
	}

	public NoContractDefinitionException(final Class<?> clazz) {
		super("Extension does not provide any contract definition: " + clazz.getCanonicalName());
	}
}
