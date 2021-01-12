package org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.scenariobehavior;

import java.util.Optional;

import org.palladiosimulator.pcm.usagemodel.AbstractUserAction;
import org.palladiosimulator.pcm.usagemodel.ScenarioBehaviour;

import com.google.common.base.Preconditions;

/**
 * This class contains a basic and concrete implementation of the {@link ScenarioBehaviorContext}
 * interface. Classes that are in some sort context holders can extend this class instead of
 * manually implementing the interface.
 * 
 * @author Julijan Katic
 * @version 1.0
 */
public class GeneralScenarioBehaviorContext implements ScenarioBehaviorContext {
	
	private final ScenarioBehaviour scenarioBehavior;
	private Optional<ScenarioBehaviorContext> parent;
	private final Optional<AbstractUserAction> nextAction;
	
	/**
	 * Constructor of this abstract class. Will set all the attributes according to the parameters: If {@code parent}
	 * is null, then an empty optional will be present.
	 * 
	 * @param scenarioBehavior The scenario behavior hold by this context. Must not be null.
	 * @param parent The parent context. If null, it will be converted to an empty optional later.
	 * @param nextAction the next action that occurs. If this is non-null, then parent must also be non-null.
	 */
	public GeneralScenarioBehaviorContext(final ScenarioBehaviour scenarioBehavior, 
										   final ScenarioBehaviorContext parent,
										   final AbstractUserAction nextAction) {
		Preconditions.checkArgument(scenarioBehavior != null, "The ScenarioBehavior must not be null");
		/* Precondition follows from (nextAction != null ==> parent != null) which is equivalent to (nextAction == null || parent != null) */
		Preconditions.checkArgument(nextAction == null || parent != null, "When nextAction is set, then its corresponding parent must also be set.");
		this.scenarioBehavior = scenarioBehavior;
		this.setParent(parent);
		if (nextAction == null) {
			this.nextAction = Optional.empty();
		} else {
			this.nextAction = Optional.of(nextAction);
		}
	}
	
	@Override
	public ScenarioBehaviour getScenarioBehavior() {
		return scenarioBehavior;
	}

	@Override
	public Optional<ScenarioBehaviorContext> getParent() {
		return parent;
	}
	
	@Override
	public void setParent(final ScenarioBehaviorContext parentContext) {
		if (parentContext == null) {
			this.parent = Optional.empty();
		} else {
			this.parent = Optional.of(parentContext);
		}
	}

	@Override
	public Optional<AbstractUserAction> getNextAction() {
		return this.nextAction;
	}

}
