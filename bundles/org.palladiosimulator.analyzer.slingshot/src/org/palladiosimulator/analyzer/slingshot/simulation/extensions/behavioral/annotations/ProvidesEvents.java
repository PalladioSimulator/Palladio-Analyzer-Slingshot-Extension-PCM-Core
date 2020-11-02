package org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;

/**
 * This annotation is used to specify which events are saved to a collection.
 * 
 * @author Julijan Katic
 */
@Documented
@Retention(SOURCE)
@Target({ METHOD })
public @interface ProvidesEvents {

	/** The classes that are allowed to be passed onto the set. */
	public Class<? extends DESEvent>[] value();

}
