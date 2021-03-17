package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.seff.behaviorcontext;

import java.util.Optional;

import org.palladiosimulator.pcm.seff.AbstractAction;

/**
 * Interface describing the proxy.
 * 
 * @author Julijan Katicß
 *
 */
public interface BehaviorContextProxy {

	Optional<AbstractAction> getSuccessor();

	AbstractAction getNextAction();

	SeffBehaviorHolder getCurrentProcessedBehavior();

	boolean hasFinished();

	Optional<SeffBehaviorHolder> getParent();
}
