package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities;

import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.resourcetype.CommunicationLinkResourceType;
import org.palladiosimulator.pcm.resourcetype.ProcessingResourceType;
import org.palladiosimulator.pcm.resourcetype.ResourceType;

/**
 * The resource request context entity is the entry for requesting resources. It
 * asks onto which component a certain resource was requested. This information
 * can be used for the allocation context to find the right resource container
 * and simulate the resource. It also holds information about the demand.
 * 
 * @author Julijan Katic
 */
public class ActiveResourceRequestContext {

	private final ResourceType resourceType;
	private final AssemblyContext assemblyContext;
	private final double demand;

	/**
	 * Instantiates the entity.
	 * 
	 * @param resourceType The resource that is requested.
	 * @param component    The component to which the resource request belongs.
	 * @param demand       The demand requested.
	 */
	public ActiveResourceRequestContext(final ResourceType resourceType, final AssemblyContext assemblyContext,
	        final double demand) {
		this.assemblyContext = assemblyContext;
		this.demand = demand;
		this.resourceType = resourceType;
	}

	public ResourceType getResourceType() {
		return resourceType;
	}

	public AssemblyContext getComponent() {
		return assemblyContext;
	}

	public double getDemand() {
		return demand;
	}

	/**
	 * Returns whether the resource is a processing resource.
	 * 
	 * @return true iff the resource is a {@code ProcessingResourceType}.
	 */
	public boolean isProcessingResource() {
		return this.resourceType instanceof ProcessingResourceType;
	}

	/**
	 * Returns whether the resource is a communication resource.
	 * 
	 * @return true iff the resource is a {@code CommunicationLinkResource}.
	 */
	public boolean isCommunicationLinkResourceType() {
		return this.resourceType instanceof CommunicationLinkResourceType;
	}
}
