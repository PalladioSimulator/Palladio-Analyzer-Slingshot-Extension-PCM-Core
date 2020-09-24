package org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities;

import org.palladiosimulator.pcm.repository.OperationProvidedRole;
import org.palladiosimulator.pcm.repository.OperationSignature;

/**
 * A UserRequest defines the interface needed for the creation of UserRequests. It is compound of
 * the User, the PCM OperationProvidedRole and the OperationSignature.
 * 
 * @author Julijan Katic
 */
public class UserRequest {

	private final User user;
	private final OperationProvidedRole operationProvidedRole;
	private final OperationSignature operationSignature;
	
	public UserRequest(final User user, final OperationProvidedRole operationProvidedRole, final OperationSignature operationSignature) {
		super();
		this.user = user;
		this.operationProvidedRole = operationProvidedRole;
		this.operationSignature = operationSignature;
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


}
