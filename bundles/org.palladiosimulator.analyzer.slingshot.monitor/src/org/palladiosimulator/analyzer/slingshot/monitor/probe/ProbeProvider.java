package org.palladiosimulator.analyzer.slingshot.monitor.probe;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.palladiosimulator.probeframework.probes.Probe;

/**
 * Tells the runtime environment that a method provides a Probe for a certain
 * event. This annotation should be used if the event cannot be directly
 * annotated with {@link ProbeTrigger}, or if there should be any calculation
 * first done.
 * 
 * The method should return an instance of {@link Probe} and have the event as
 * the only parameter, i.e.
 * 
 * <pre>
 * &#64;ProbeProvider
 * public Probe userStartedCurrentSimulationTimeProbe(final UserStarted userStarted) {
 * 	return new EventCurrentSimulationTime(userStarted);
 * }
 * </pre>
 * 
 * If providers are used, then these should be in a injectable class (or a class
 * with a no-arg constructor) and referenced by the extension point (TODO: Find
 * Extension Point).
 * 
 * @author Julijan Katic
 *
 */
@Documented
@Retention(RUNTIME)
@Target(METHOD)
public @interface ProbeProvider {

}
