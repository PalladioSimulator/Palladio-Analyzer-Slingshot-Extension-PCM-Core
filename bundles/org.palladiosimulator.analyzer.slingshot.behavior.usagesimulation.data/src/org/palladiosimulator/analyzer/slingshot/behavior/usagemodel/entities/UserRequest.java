package org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities;

import org.eclipse.emf.common.util.EList;
import org.palladiosimulator.pcm.parameter.VariableUsage;
import org.palladiosimulator.pcm.repository.OperationProvidedRole;
import org.palladiosimulator.pcm.repository.OperationSignature;

/**
 * A UserRequest defines the interface needed for the creation of UserRequests.
 * It is compound of the User, the PCM OperationProvidedRole and the
 * OperationSignature.
 * 
 * @author Julijan Katic
 */
public class UserRequest {

	private final User user;
	private final OperationProvidedRole operationProvidedRole;
	private final OperationSignature operationSignature;
	private final EList<VariableUsage> variableUsages;

	public UserRequest(final User user, final OperationProvidedRole operationProvidedRole,
	        final OperationSignature operationSignature, final EList<VariableUsage> variableUsages) {
		super();
		this.user = user;
		this.operationProvidedRole = operationProvidedRole;
		this.operationSignature = operationSignature;
		this.variableUsages = variableUsages;
	}

	public User getUser() {
		return user;
	}

	public OperationProvidedRole getOperationProvidedRole() {
		return operationProvidedRole;
	}

	public OperationSignature getOperationSignature() {
		return operationSignature;
	}

	public EList<VariableUsage> getVariableUsages() {
		return variableUsages;
	}

}
