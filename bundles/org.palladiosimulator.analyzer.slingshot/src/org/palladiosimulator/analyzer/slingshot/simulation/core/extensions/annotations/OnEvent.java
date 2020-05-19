package org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.annotations;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;

@Retention(RetentionPolicy.RUNTIME)
@Repeatable(OnEvent.OnEvents.class)
@Documented
@Target(TYPE)
public @interface OnEvent {

	Class<? extends DESEvent>[] outputEventType();

	EventCardinality cardinality() default EventCardinality.SINGLE;

	Class<? extends DESEvent> eventType();
	
	@Retention(RetentionPolicy.RUNTIME)
	@Documented
	@Target(TYPE)
	@interface OnEvents {
		OnEvent[] value();
	}
	
}
