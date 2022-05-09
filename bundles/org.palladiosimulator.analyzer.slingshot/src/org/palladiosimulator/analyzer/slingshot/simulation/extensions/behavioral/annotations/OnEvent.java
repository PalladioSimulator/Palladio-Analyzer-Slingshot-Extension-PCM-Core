package org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;

/**
 * Annotates a class that is needed for contract checking. It specifies a
 * contract for incoming events and enforces it at runtime.
 * 
 * @author Julijan Katic
 */
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(OnEvent.OnEvents.class)
@Documented
@Target(TYPE)
public @interface OnEvent {

	/**
	 * The types of events that are expected when {@link #when()} has been
	 * triggered.
	 */
	Class<? extends DESEvent>[] then();

	/**
	 * The cardinality specifying how many events are catched within a contract.
	 * Defaults to {@link EventCardinality.SINGLE}.
	 */
	EventCardinality cardinality() default EventCardinality.SINGLE;

	/**
	 * Specifies the incoming event type.
	 */
	Class<? extends DESEvent> when();

	/**
	 * Specifies the type parameters when the {@link #when()} type is of a generic
	 * type.
	 */
	Class<?>[] whenReified() default {};

	@Retention(RetentionPolicy.RUNTIME)
	@Documented
	@Target(TYPE)
	public @interface OnEvents {
		OnEvent[] value();
	}

}
