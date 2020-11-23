package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities;

import org.eclipse.emf.common.util.EList;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.User;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.UserContextEntityHolder;
import org.palladiosimulator.pcm.parameter.VariableUsage;
import org.palladiosimulator.pcm.repository.RequiredRole;
import org.palladiosimulator.pcm.repository.Signature;

/**
 * A general entry request is a more general request for a resource demanding
 * service effect specification entry. Unlike {@link UserEntryRequest}, this
 * holds the assembly context and the required role as this form of request is
 * typically done by another RDSeff itself.
 * 
 * @author Julijan Katic
 */
public class GeneralEntryRequest extends UserContextEntityHolder {

	/** Specifies which on with required role the service is called. */
	private final RequiredRole requiredRole;

	/** Specifies which service is called */
	private final Signature signature;

	/** The variables for the service call. */
	private final EList<VariableUsage> inputVariableUsages;

	public GeneralEntryRequest(final User user, final RequiredRole requiredRole, final Signature signature,
	        final EList<VariableUsage> inputVariableUsages) {
		super(user);
		this.requiredRole = requiredRole;
		this.signature = signature;
		this.inputVariableUsages = inputVariableUsages;
	}

	public RequiredRole getRequiredRole() {
		return requiredRole;
	}

	public Signature getSignature() {
		return signature;
	}

	public EList<VariableUsage> getInputVariableUsages() {
		return inputVariableUsages;
	}

}