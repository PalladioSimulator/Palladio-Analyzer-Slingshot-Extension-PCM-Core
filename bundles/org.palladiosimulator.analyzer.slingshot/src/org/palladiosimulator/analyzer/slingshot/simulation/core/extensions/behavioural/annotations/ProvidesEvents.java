package org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.behavioural.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.utils.DESEventSet;

/**
 * This annotation is used to specify which events are saved to a {@link DESEventSet}. If an event is passed
 * that is not allowed, the annotation processor should mark it as incorrect.
 * 
 * @author Julijan Katic
 */
@Documented
@Retention(RUNTIME)
@Target({ FIELD })
public @interface ProvidesEvents {
	
	/** The classes that are allowed to be passed onto the set. */
	public Class<? extends DESEvent>[] value();
	
}
