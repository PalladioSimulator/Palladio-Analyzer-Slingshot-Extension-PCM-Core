package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities;

import org.eclipse.emf.common.util.EList;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.User;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.UserContextEntityHolder;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.parameter.VariableUsage;
import org.palladiosimulator.pcm.repository.ProvidedRole;
import org.palladiosimulator.pcm.repository.Signature;

/**
 * This entity is used for having a context for the resource interpreter.
 * 
 * @author Julijan Katic
 */
public class RepositoryInterpretationContext extends UserContextEntityHolder {

	private ProvidedRole providedRole;
	private Signature signature;
	private AssemblyContext assemblyContext;
	private EList<VariableUsage> inputParameters;

	public RepositoryInterpretationContext(final User user) {
		super(user);
		// TODO Auto-generated constructor stub
	}

	public ProvidedRole getProvidedRole() {
		return providedRole;
	}

	public void setProvidedRole(final ProvidedRole providedRole) {
		this.providedRole = providedRole;
	}

	public Signature getSignature() {
		return signature;
	}

	public void setSignature(final Signature signature) {
		this.signature = signature;
	}

	public EList<VariableUsage> getInputParameters() {
		return inputParameters;
	}

	public void setInputParameters(final EList<VariableUsage> variableUsages) {
		this.inputParameters = variableUsages;
	}

	public AssemblyContext getAssemblyContext() {
		return assemblyContext;
	}

	public void setAssemblyContext(final AssemblyContext assemblyContext) {
		this.assemblyContext = assemblyContext;
	}

}
