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
 * Instead of a simple Role, a {@link ProvidedRole} is needed.
 * 
 * @author Julijan Katic
 */
public class UserEntryRequest extends UserContextEntityHolder {

	private final ProvidedRole providedRole;
	private final Signature signature;
	private final EList<VariableUsage> variableUsages;

	public UserEntryRequest(final User user, final ProvidedRole providedRole, final Signature signature,
	        final EList<VariableUsage> variableUsages) {
		super(user);
		this.providedRole = EcoreUtil.copy(providedRole);
		this.signature = EcoreUtil.copy(signature);
		this.variableUsages = variableUsages;
	}

	public ProvidedRole getProvidedRole() {
		return EcoreUtil.copy(providedRole);
	}

	public Signature getSignature() {
		return EcoreUtil.copy(signature);
	}

	public EList<VariableUsage> getVariableUsages() {
		return variableUsages;
	}

}
