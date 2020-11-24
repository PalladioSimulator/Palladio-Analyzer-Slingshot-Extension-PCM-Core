package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities;

import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.User;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.seff.AbstractAction;

/**
 * The interpretation entity containing information for the interpreter needed
 * to continue interpretation. Besides the assembly context and user, this
 * entity also holds an abstract action onto which the Switch can be called.
 * 
 * @author Julijan Katic
 */
public class SeffInterpretationEntity extends SeffContextHolderEntity {

	/** The abstract action to interpret. */
	private final AbstractAction currentAction;

	/**
	 * Instantiates the entity with the necessary information.
	 * 
	 * @param assemblyContext The context where the SEFF belongs to.
	 * @param user            The user who called the SEFF service.
	 * @param currentAction   The abstract action that needs to be interpreted.
	 */
	public SeffInterpretationEntity(final AssemblyContext assemblyContext, final User user,
	        final AbstractAction currentAction) {
		super(assemblyContext, user);
		this.currentAction = currentAction;
	}

	/**
	 * Returns the abstract action that needs to be interpreted.
	 * 
	 * @return The abstract action needed for interpretation.
	 */
	public AbstractAction getCurrentAction() {
		return currentAction;
	}

}
