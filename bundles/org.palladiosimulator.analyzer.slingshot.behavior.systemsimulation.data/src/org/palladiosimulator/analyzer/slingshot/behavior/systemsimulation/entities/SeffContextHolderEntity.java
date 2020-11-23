package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities;

import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.User;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.UserContextEntityHolder;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;

/**
 * For a seff interpretation, it is always needed to now to which assembly
 * context or component it belongs, as well as the user context.
 * 
 * @author Julijan Katic
 */
public class SeffContextHolderEntity extends UserContextEntityHolder {

	private final AssemblyContext assemblyContext;

	public SeffContextHolderEntity(final AssemblyContext assemblyContext, final User user) {
		super(user);
		this.assemblyContext = assemblyContext;
	}

	public AssemblyContext getAssemblyContext() {
		return assemblyContext;
	}

}
