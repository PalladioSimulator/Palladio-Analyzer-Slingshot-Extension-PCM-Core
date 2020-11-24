package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities;

import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.User;
import org.palladiosimulator.pcm.core.PCMRandomVariable;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.resourcetype.ProcessingResourceType;
import org.palladiosimulator.pcm.seff.seff_performance.ParametricResourceDemand;

/**
 * A resource demand request entity represents the data in the
 * {@code InternalAction} of a RDSeff. This entity is immutable.
 * 
 * @author Julijan Katic
 */
public final class ResourceDemandRequest extends SeffContextHolderEntity {

	/** The random variable specifying the demand of the resource. */
	private final ParametricResourceDemand parameter;

	/** The resource to be demanded. */
	private final ProcessingResourceType requiredResource;

	/** The demand of the request as a random variable. */
	private final PCMRandomVariable demand;

	/**
	 * Instantiates the resource demand request with the necessary information.
	 * 
	 * @param assemblyContext  The context for which the resource demand is meant.
	 *                         (super)
	 * @param user             The user requesting the resource demand. (super)
	 * @param parameter        The parameter
	 * @param requiredResource The resource type that is demanded.
	 * @param specification    The demand of the resource.
	 * @see SeffContextHolderEntity#SeffContextHolderEntity(AssemblyContext, User)
	 */
	public ResourceDemandRequest(final AssemblyContext assemblyContext, final User user,
	        final ParametricResourceDemand parameter,
	        final ProcessingResourceType requiredResource,
	        final PCMRandomVariable specification) {
		super(assemblyContext, user);
		this.parameter = parameter;
		this.requiredResource = requiredResource;
		this.demand = specification;
	}

	public ParametricResourceDemand getParameter() {
		return parameter;
	}

	public ProcessingResourceType getRequiredResource() {
		return requiredResource;
	}

	public PCMRandomVariable getDemand() {
		return demand;
	}

}
