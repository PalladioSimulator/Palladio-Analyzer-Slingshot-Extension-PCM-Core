package org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;

/**
 * Annotation that specifies a contract on the events themselves.
 * 
 * @author Julijan Katic
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface EventContract {

	/**
	 * Specifies how many times the event can be published. If the value is 0 (that
	 * is {@link PUBLISH_INFINITY}), then the event can be published infinitely many
	 * times. Defaults to {@link PUBLISH_INFINITY}.
	 */
	int maximalPublishing() default PUBLISH_INFINITY;

	/**
	 * Specifies which events (and their respecting subclasses) are allowed to cause
	 * the next event. This can be useful for compile time checking that there are
	 * no events causing this that is not explicitly specified.
	 * 
	 * The default is an array with the single element of {@link DESEvent}, meaning
	 * that every event can cause this event.
	 */
	Class<? extends DESEvent>[] allowedCausers() default { DESEvent.class };

	/**
	 * The number specifying that the events can be published infinitely many times.
	 */
	int PUBLISH_INFINITY = 0;

}
