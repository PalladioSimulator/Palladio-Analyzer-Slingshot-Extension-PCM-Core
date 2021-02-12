package org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.scenariobehavior;

import java.util.Optional;

import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.interpretationcontext.UserInterpretationContext;
import org.palladiosimulator.pcm.usagemodel.AbstractUserAction;
import org.palladiosimulator.pcm.usagemodel.ScenarioBehaviour;

public class RootScenarioContext extends UsageScenarioBehaviorContext {
	
	private boolean scenarioStarted = true;

	public RootScenarioContext(UserInterpretationContext referencedContext, ScenarioBehaviour scenarioBehavior) {
		this(builder()
				.withNextAction(Optional.empty())
				.withParent(Optional.empty())
				.withReferencedContext(referencedContext)
				.withScenarioBehavior(scenarioBehavior));
	}
	
	public RootScenarioContext(final Builder builder) {
		super(builder);
	}

	@Override
	public boolean mustRepeatScenario() {
		return scenarioStarted;
	}
	
	@Override
	public AbstractUserAction startScenario() {
		final AbstractUserAction userAction = super.startScenario();
		this.scenarioStarted = false;
		return userAction;
	}
	
	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder extends BaseBuilder<RootScenarioContext, Builder> {

		@Override
		public RootScenarioContext build() {
			return new RootScenarioContext(this);
		}
		
	}
}
