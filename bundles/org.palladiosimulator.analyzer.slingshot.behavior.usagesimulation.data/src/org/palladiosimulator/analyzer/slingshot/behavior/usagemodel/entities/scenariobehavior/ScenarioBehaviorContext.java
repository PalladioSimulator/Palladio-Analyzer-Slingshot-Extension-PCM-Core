package org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.scenariobehavior;

import java.util.Optional;

import org.palladiosimulator.pcm.usagemodel.AbstractUserAction;
import org.palladiosimulator.pcm.usagemodel.ScenarioBehaviour;

/**
 * A context holder that holds a scenario behavior. This is useful
 * as the a scenario behavior can be described within another
 * (parent) scenario behavior, such as Loops and Branches.
 * 
 * @author Julijan Katic
 * @version 1.0
 */
public interface ScenarioBehaviorContext {

	/**
	 * Returns the scenario behavior that this context holds.
	 * Should be non-null.
	 * 
	 * @return non-null scenario behavior of this context holder.
	 */
	ScenarioBehaviour getScenarioBehavior();
	
	/**
	 * Returns an optional of the parent scenario behavior context.
	 * This will be empty if there is no parent.
	 * 
	 * @return optional with the parent context holder if it exists, otherwise empty.
	 */
	Optional<ScenarioBehaviorContext> getParent();
	
	/**
	 * Sets the parent context of this behavior context. Will be converted into
	 * an Optional.
	 * 
	 * @param parentContext the parent context, must be non-null.
	 * @see #getParent()
	 */
	void setParent(ScenarioBehaviorContext parentContext);
	
	/**
	 * Returns an optional of the next action in the parent context that can
	 * be executed after this scenario behavior is finished.
	 * 
	 * @return the next user action that follows after the inner scenario behavior.
	 *         If the scenario behavior has no parent or if no action follows afterwards,
	 *         this will return an empty optional.
	 */
	Optional<AbstractUserAction> getNextAction();
	
}
