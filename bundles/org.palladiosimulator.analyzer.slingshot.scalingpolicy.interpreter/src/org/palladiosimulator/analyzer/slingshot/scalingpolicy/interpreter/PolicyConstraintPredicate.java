package org.palladiosimulator.analyzer.slingshot.scalingpolicy.interpreter;

import java.util.function.Function;

import org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.TriggerContext;
import org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.result.ConstraintResult;

/**
 * A policy constraint predicate tells whether the adjustment can be executed.
 * 
 * @author Julijan Katic
 */
@FunctionalInterface
public interface PolicyConstraintPredicate extends Function<TriggerContext, ConstraintResult> {

}
