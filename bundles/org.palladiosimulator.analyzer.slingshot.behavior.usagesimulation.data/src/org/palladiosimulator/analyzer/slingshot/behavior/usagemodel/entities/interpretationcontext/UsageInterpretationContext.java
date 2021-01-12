package org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.interpretationcontext;

import com.google.common.collect.ImmutableList;
import javax.annotation.processing.Generated;

/**
 * This class represents the context of a usage model that is needed
 * to be interpreted.
 * 
 * @author Julijan Katic
 */
public class UsageInterpretationContext {

	private final ImmutableList<UsageScenarioInterpretationContext> usageScenariosContexts;

	@Generated("SparkTools")
	private UsageInterpretationContext(Builder builder) {
		this.usageScenariosContexts = builder.usageScenariosContexts;
	}
	
	public ImmutableList<UsageScenarioInterpretationContext> getUsageScenarioContexts() {
		return this.usageScenariosContexts;
	}

	/**
	 * Creates builder to build {@link UsageInterpretationContext}.
	 * @return created builder
	 */
	@Generated("SparkTools")
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Builder to build {@link UsageInterpretationContext}.
	 */
	@Generated("SparkTools")
	public static final class Builder {
		private ImmutableList<UsageScenarioInterpretationContext> usageScenariosContexts;

		private Builder() {
		}

		public Builder withUsageScenariosContexts(
				ImmutableList<UsageScenarioInterpretationContext> usageScenariosContexts) {
			this.usageScenariosContexts = usageScenariosContexts;
			return this;
		}

		public UsageInterpretationContext build() {
			return new UsageInterpretationContext(this);
		}
	}
	
	
	
}
