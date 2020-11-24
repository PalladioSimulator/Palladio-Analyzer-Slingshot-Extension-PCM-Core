package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities;

import org.eclipse.emf.common.util.EList;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.User;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.UserContextEntityHolder;
import org.palladiosimulator.pcm.parameter.VariableUsage;
import org.palladiosimulator.pcm.repository.RequiredRole;
import org.palladiosimulator.pcm.repository.Signature;

/**
 * A general entry request is a more general request for a resource demanding
 * service effect specification entry. Unlike {@link User}, this
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

	/** The input variables for the service call. */
	private final EList<VariableUsage> inputVariableUsages;

	/**
	 * Instantiates a GeneralEntryRequest.
	 * 
	 * @param user                From the super class: The user on which the
	 *                            service is called from.
	 * @param requiredRole        The required role to call.
	 * @param signature           The signature of the call.
	 * @param inputVariableUsages The input variables for the call.
	 */
	public GeneralEntryRequest(final User user, final RequiredRole requiredRole, final Signature signature,
	        final EList<VariableUsage> inputVariableUsages) {
		super(user);
		this.requiredRole = requiredRole;
		this.signature = signature;
		this.inputVariableUsages = inputVariableUsages;
	}

	/**
	 * Returns the required role where the service is called.
	 * 
	 * @return the required role.
	 */
	public RequiredRole getRequiredRole() {
		return requiredRole;
	}

	/**
	 * Returns the signature of the service. It should be present on the interface
	 * from the required role.
	 * 
	 * @return the signature of the call.
	 */
	public Signature getSignature() {
		return signature;
	}

	/**
	 * Returns the list of the input variables for the call.
	 * 
	 * @return the list of input variables.
	 */
	public EList<VariableUsage> getInputVariableUsages() {
		return inputVariableUsages;
	}

}