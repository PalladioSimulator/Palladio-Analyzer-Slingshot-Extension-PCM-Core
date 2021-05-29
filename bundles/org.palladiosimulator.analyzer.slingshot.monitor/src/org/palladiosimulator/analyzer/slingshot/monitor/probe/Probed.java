package org.palladiosimulator.analyzer.slingshot.monitor.probe;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.palladiosimulator.analyzer.slingshot.monitor.calculator.CalculatorProvider;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;

/**
 * This annotation is used in a parameter of a {@link CalculatorProvider} to
 * initialize the probes lazily. The probe must annotate the parameter of type
 * {@link DESEventProbe}. This annotation will then ensure that the parameter
 * that is used will return the {@code Class<?>} in
 * {@link DESEventProbe#getEventType()} as specified in this {@link #value()}.
 * 
 * @author Julijan Katic
 *
 */
@Documented
@Retention(RUNTIME)
@Target(PARAMETER)
public @interface Probed {

	/** The event of which the probe should conform to. */
	public Class<? extends DESEvent> value();

}
