package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.resource;

import java.util.Optional;

import javax.annotation.processing.Generated;

import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.seff.SEFFInterpretationContext;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.User;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.repository.PassiveResource;
import org.palladiosimulator.pcm.seff.seff_performance.ParametricResourceDemand;

/**
 * 
 * @author Julijan Katic
 */
public final class ResourceDemandRequest {

	public enum ResourceType {
		ACTIVE, PASSIVE
	}
	
	private final AssemblyContext assemblyContext;
	
	private final SEFFInterpretationContext seffInterpretationContext;
	
	private final ParametricResourceDemand parametricResourceDemand;
	
	private final ResourceType resourceType;
	
	private final Optional<PassiveResource> passiveResource;

	@Generated("SparkTools")
	private ResourceDemandRequest(final Builder builder) {
		this.assemblyContext = builder.assemblyContext;
		this.seffInterpretationContext = builder.seffInterpretationContext;
		this.parametricResourceDemand = builder.parametricResourceDemand;
		this.resourceType = builder.resourceType;
		this.passiveResource = builder.passiveResource;
	}

	/**
	 * @return the assemblyContext
	 */
	public AssemblyContext getAssemblyContext() {
		return this.assemblyContext;
	}

	/**
	 * @return the seffInterpretationContext
	 */
	public SEFFInterpretationContext getSeffInterpretationContext() {
		return this.seffInterpretationContext;
	}

	/**
	 * @return the parametricResourceDemand
	 */
	public ParametricResourceDemand getParametricResourceDemand() {
		return this.parametricResourceDemand;
	}
	
	public ResourceType getResourceType() {
		return this.resourceType;
	}
	
	public User getUser() {
		return this.seffInterpretationContext.getRequestProcessingContext().getUser();
	}
	
	public Optional<PassiveResource> getPassiveResource() {
		return this.passiveResource;
	}

	/**
	 * Creates builder to build {@link ResourceDemandRequest}.
	 * @return created builder
	 */
	@Generated("SparkTools")
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Builder to build {@link ResourceDemandRequest}.
	 */
	@Generated("SparkTools")
	public static final class Builder {
		private AssemblyContext assemblyContext;
		private SEFFInterpretationContext seffInterpretationContext;
		private ParametricResourceDemand parametricResourceDemand;
		private ResourceType resourceType = ResourceType.ACTIVE;
		private Optional<PassiveResource> passiveResource = Optional.empty();

		private Builder() {
		}

		public Builder withAssemblyContext(final AssemblyContext assemblyContext) {
			this.assemblyContext = assemblyContext;
			return this;
		}

		public Builder withSeffInterpretationContext(final SEFFInterpretationContext seffInterpretationContext) {
			this.seffInterpretationContext = seffInterpretationContext;
			return this;
		}

		public Builder withParametricResourceDemand(final ParametricResourceDemand parametricResourceDemand) {
			this.parametricResourceDemand = parametricResourceDemand;
			return this;
		}

		public Builder withResourceType(final ResourceType resourceType) {
			this.resourceType = resourceType;
			return this;
		}
		
		public Builder withPassiveResource(final PassiveResource passiveResource) {
			if (passiveResource == null) {
				this.passiveResource = Optional.empty();
			} else {
				this.passiveResource = Optional.of(passiveResource);
			}
			return this;
		}

		public ResourceDemandRequest build() {
			return new ResourceDemandRequest(this);
		}
	}
	
	
}
