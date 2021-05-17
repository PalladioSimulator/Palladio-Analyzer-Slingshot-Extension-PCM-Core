package org.palladiosimulator.analyzer.slingshot.monitor.calculator;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.probeframework.calculator.Calculator;
import org.palladiosimulator.probeframework.probes.Probe;

/**
 * Defines that a calculation should take place on certain {@link DESEvents} if
 * a certain {@link Probe} is attached.
 * <p>
 * Internally, the monitor will hold different instances of specified
 * calculators and let them listen to the events with the specified probes. As
 * soon as all the probes are collected that the calculator needs, the
 * calculator will eventually publish a new measurement that the recorder then
 * can take.
 * 
 * @author Julijan Katic
 *
 */
@Documented
@Retention(RUNTIME)
@Target(METHOD)
public @interface Calculate {

	/**
	 * The array of {@link DESEvent} events that the calculator should listen to.
	 */
	public Class<? extends DESEvent>[] on();

	/**
	 * Specifies if the calculator should only listen to the events in the order as
	 * specified by {@link #on()}.
	 */
	public boolean ordered() default false;

	/**
	 * Specifies the probe the events should be attached on.
	 */
	public Class<? extends Probe>[] probes();

	/**
	 * The calculator of which an instance should be created and used as soon as the
	 * probes are available.
	 * 
	 * @return
	 */
	public Class<? extends Calculator> calculator();
}
