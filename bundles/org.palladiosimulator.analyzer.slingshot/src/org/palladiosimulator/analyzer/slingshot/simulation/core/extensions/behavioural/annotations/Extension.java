package org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.behavioural.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.behavioural.decorators.AbstractDecoratedSimulationBehaviorProvider;

/**
 * This annotation provides a way to define an extension of a system. As of now, it used to define the
 * behaviour of the system. This annotation will create a new class at compile-time that inherits from
 * {@link AbstractDecoratedSimulationBehaviorProvider} and overrides the necessary things.
 * 
 * @author Julijan Katic
 */
@Retention(SOURCE)
@Target(TYPE)
public @interface Extension {

}
