package org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.behavioural.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.behavioural.SimulationBehaviourExtension;

/**
 * Explicitly marks a method that it handles an event and makes it visible to the contract checker.
 * Typically, in a {@link SimulationBehaviourExtension} subclass, when a method name starts with "on", 
 * then the method is automatically marked and visible to the contract checker.
 * 
 * @author Julijan Katic
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface EventMethod {

}
