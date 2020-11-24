package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.User;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.UserContextEntityHolder;
import org.palladiosimulator.pcm.parameter.VariableUsage;
import org.palladiosimulator.pcm.repository.ProvidedRole;
import org.palladiosimulator.pcm.repository.Signature;

/**
 * This kind of request is especially designed for a entry call to the system.
 * Besides the user who requests an entry to the system, it also know which
 * service is called and what variables are used.
 * 
 * @author Julijan Katic
 */
public class UserEntryRequest extends UserContextEntityHolder {

	/** The role from which the service is called. */
	private final ProvidedRole providedRole;

	/** The service's signature that is called. */
	private final Signature signature;

	/** The variables used for the call into the service. */
	private final EList<VariableUsage> variableUsages;

	/**
	 * Instantiates the user entry request with the necessary information.
	 * 
	 * @param user           The user who called the service.
	 * @param providedRole   The provided role from which the service is called.
	 * @param signature      The service's signature.
	 * @param variableUsages The variables that is being used for the call into the
	 *                       system.
	 */
	public UserEntryRequest(final User user, final ProvidedRole providedRole, final Signature signature,
	        final EList<VariableUsage> variableUsages) {
		super(user);
		this.providedRole = EcoreUtil.copy(providedRole);
		this.signature = EcoreUtil.copy(signature);
		this.variableUsages = variableUsages;
	}

	/**
	 * Returns the provided role of the request. The provided role gives information
	 * about the location of the service.
	 * 
	 * @return the provided role where the service lies and which the user calls.
	 */
	public ProvidedRole getProvidedRole() {
		return EcoreUtil.copy(providedRole);
	}

	/**
	 * Returns the signature of the service.
	 * 
	 * @return the signature of the service which the user calls.
	 */
	public Signature getSignature() {
		return EcoreUtil.copy(signature);
	}

	/**
	 * Returns the list of variables that is used for the call.
	 * 
	 * @return The list of variables for the call.
	 */
	public EList<VariableUsage> getVariableUsages() {
		return variableUsages;
	}

}
