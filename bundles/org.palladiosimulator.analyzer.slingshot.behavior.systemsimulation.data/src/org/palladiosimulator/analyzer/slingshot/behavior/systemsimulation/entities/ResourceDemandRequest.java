package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities;

import org.palladiosimulator.pcm.core.PCMRandomVariable;
import org.palladiosimulator.pcm.resourcetype.ProcessingResourceType;
import org.palladiosimulator.pcm.seff.seff_performance.ParametricResourceDemand;

/**
 * A resource demand request entity represents the data in the
 * {@code InternalAction} of a RDSeff. This entity is immutable.
 * 
 * @author Julijan Katic
 */
public final class ResourceDemandRequest {

	/** The random variable specifying the demand of the resource. */
	private final ParametricResourceDemand parameter;

	/** The resource to be demanded. */
	private final ProcessingResourceType requiredResource;

	private final PCMRandomVariable demand;

	public ResourceDemandRequest(final ParametricResourceDemand parameter,
	        final ProcessingResourceType requiredResource,
	        final PCMRandomVariable specification) {
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
