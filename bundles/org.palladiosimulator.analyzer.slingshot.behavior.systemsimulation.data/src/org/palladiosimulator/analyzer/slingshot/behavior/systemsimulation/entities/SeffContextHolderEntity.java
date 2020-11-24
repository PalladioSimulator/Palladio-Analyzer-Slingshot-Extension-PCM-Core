package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities;

import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.User;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.UserContextEntityHolder;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;

/**
 * The context holder for the SEFF interpretation.
 * 
 * For a SEFF interpretation to work, it always needs to know to which assembly
 * context or component the SEFF belongs, as well as the user context that
 * called the service.
 * 
 * @author Julijan Katic
 */
public class SeffContextHolderEntity extends UserContextEntityHolder {

	/** The assembly context on which the SEFF is defined. */
	private final AssemblyContext assemblyContext;

	/**
	 * Instantiates the entity with the required information.
	 * 
	 * @param assemblyContext The assembly context on which the SEFF is defined.
	 * @param user            The user calling the SEFF.
	 */
	public SeffContextHolderEntity(final AssemblyContext assemblyContext, final User user) {
		super(user);
		this.assemblyContext = assemblyContext;
	}

	/**
	 * Returns the specified assembly context. This is the entity on which the SEFF
	 * is defined.
	 * 
	 * @return the assembly context with the defined SEFF.
	 */
	public AssemblyContext getAssemblyContext() {
		return assemblyContext;
	}

}
