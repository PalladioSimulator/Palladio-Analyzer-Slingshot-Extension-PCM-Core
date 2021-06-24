package org.palladiosimulator.analyzer.slingshot.simulation.core;

import javax.annotation.processing.Generated;

import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.resourceenvironment.ResourceEnvironment;
import org.palladiosimulator.pcm.resourcetype.ResourceRepository;
import org.palladiosimulator.pcm.system.System;
import org.palladiosimulator.pcm.usagemodel.UsageModel;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * This class holds all the required models from the PCM simulation, but also
 * has the ability to let it extend with further models that might not be
 * specified by PCM currently.
 * 
 * @author Julijan Katic
 *
 */
public final class SlingshotModel extends AbstractModule {

	private final Allocation allocationModel;
	private final Repository repositoryModel;
	private final ResourceEnvironment resourceEnvironmentModel;
	private final System systemModel;
	private final UsageModel usageModel;
	private final ResourceRepository resourceRepository;

	@Generated("SparkTools")
	private SlingshotModel(final Builder builder) {
		this.allocationModel = builder.allocationModel;
		this.repositoryModel = builder.repositoryModel;
		this.resourceEnvironmentModel = builder.resourceEnvironmentModel;
		this.systemModel = builder.systemModel;
		this.usageModel = builder.usageModel;
		this.resourceRepository = builder.resourceRepository;
	}

	public SlingshotModel(final Allocation allocationModel, final Repository repositoryModel,
			final ResourceEnvironment resourceEnvironmentModel, final System systemModel, final UsageModel usageModel,
			final ResourceRepository resourceRepository) {
		this.allocationModel = allocationModel;
		this.repositoryModel = repositoryModel;
		this.resourceEnvironmentModel = resourceEnvironmentModel;
		this.systemModel = systemModel;
		this.usageModel = usageModel;
		this.resourceRepository = resourceRepository;
	}

	/**
	 * @return the allocationModel
	 */
	@Provides
	public Allocation getAllocationModel() {
		return this.allocationModel;
	}

	/**
	 * @return the repositoryModel
	 */
	@Provides
	public Repository getRepositoryModel() {
		return this.repositoryModel;
	}

	/**
	 * @return the resourceEnvironmentModel
	 */
	@Provides
	public ResourceEnvironment getResourceEnvironmentModel() {
		return this.resourceEnvironmentModel;
	}

	/**
	 * @return the systemModel
	 */
	@Provides
	public System getSystemModel() {
		return this.systemModel;
	}

	/**
	 * @return the usageModel
	 */
	@Provides
	public UsageModel getUsageModel() {
		return this.usageModel;
	}

	/**
	 * @return the resourceRepository
	 */
	@Provides
	public ResourceRepository getResourceRepository() {
		return this.resourceRepository;
	}

	/**
	 * Creates builder to build {@link SlingshotModel}.
	 * 
	 * @return created builder
	 */
	@Generated("SparkTools")
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Builder to build {@link SlingshotModel}.
	 */
	@Generated("SparkTools")
	public static final class Builder {
		private Allocation allocationModel;
		private Repository repositoryModel;
		private ResourceEnvironment resourceEnvironmentModel;
		private System systemModel;
		private UsageModel usageModel;
		private ResourceRepository resourceRepository;

		private Builder() {
		}

		/**
		 * Builder method for allocationModel parameter.
		 * 
		 * @param allocationModel field to set
		 * @return builder
		 */
		public Builder withAllocationModel(final Allocation allocationModel) {
			this.allocationModel = allocationModel;
			return this;
		}

		/**
		 * Builder method for repositoryModel parameter.
		 * 
		 * @param repositoryModel field to set
		 * @return builder
		 */
		public Builder withRepositoryModel(final Repository repositoryModel) {
			this.repositoryModel = repositoryModel;
			return this;
		}

		/**
		 * Builder method for resourceEnvironmentModel parameter.
		 * 
		 * @param resourceEnvironmentModel field to set
		 * @return builder
		 */
		public Builder withResourceEnvironmentModel(final ResourceEnvironment resourceEnvironmentModel) {
			this.resourceEnvironmentModel = resourceEnvironmentModel;
			return this;
		}

		/**
		 * Builder method for systemModel parameter.
		 * 
		 * @param systemModel field to set
		 * @return builder
		 */
		public Builder withSystemModel(final System systemModel) {
			this.systemModel = systemModel;
			return this;
		}

		/**
		 * Builder method for usageModel parameter.
		 * 
		 * @param usageModel field to set
		 * @return builder
		 */
		public Builder withUsageModel(final UsageModel usageModel) {
			this.usageModel = usageModel;
			return this;
		}

		/**
		 * Builder method for resourceRepository parameter.
		 * 
		 * @param resourceRepository field to set
		 * @return builder
		 */
		public Builder withResourceRepository(final ResourceRepository resourceRepository) {
			this.resourceRepository = resourceRepository;
			return this;
		}

		/**
		 * Builder method of the builder.
		 * 
		 * @return built class
		 */
		public SlingshotModel build() {
			return new SlingshotModel(this);
		}
	}

}
