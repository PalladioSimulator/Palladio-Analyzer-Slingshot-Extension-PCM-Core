package org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.scenariobehavior;

import java.util.Optional;

import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.interpretationcontext.UserInterpretationContext;
import org.palladiosimulator.pcm.usagemodel.AbstractUserAction;

import com.google.common.base.Preconditions;

/**
 * This indicates for a inner scenario within a branch. A branch scenario
 * does not have to be repeated again, thus {@link #mustRepeatScenario()}
 * always returns {@code false}.
 * 
 * @author Julijan Katic
 */
public class BranchScenarioContext extends UsageScenarioBehaviorContext {

	public BranchScenarioContext(final Builder builder) {
		super(builder);
		Preconditions.checkArgument(this.getNextAction().isPresent());
	}

	@Override
	public boolean mustRepeatScenario() {
		return false;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static final class Builder extends BaseBuilder<BranchScenarioContext, Builder> {
		
		@Override
		public BranchScenarioContext build() {
			return new BranchScenarioContext(this);
		}
		
		
	}
}
