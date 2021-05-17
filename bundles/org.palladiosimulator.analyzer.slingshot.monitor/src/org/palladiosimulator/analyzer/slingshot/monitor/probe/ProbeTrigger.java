package org.palladiosimulator.analyzer.slingshot.monitor.probe;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Tells that the event can be tracked by a probe. At runtime, when the
 * annotated event is published, the probe class is triggered.
 * 
 * This annotation should be used if the event is accessible and possible to be
 * annotated. For example, for a user started event, the current simulation time
 * should be tracked:
 * 
 * <pre>
 * &#64;ProbeTrigger(EventCurrentSimulationTimeProbe.class)
 * public class UserStarted // implements DESEvent ...
 * </pre>
 * 
 * Unlike {@link ProbeProvider}, events annotated with {@code ProbeTrigger} will
 * be automatically tracked as soon as the event is published; hence, there is
 * no need for defining an extension point.
 * 
 * @author Julijan Katic
 *
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
@Repeatable(value = ProbeTrigger.ProbeTriggers.class)
public @interface ProbeTrigger {

	/**
	 * Returns the probe class which should be triggered when the event is
	 * triggered.
	 * 
	 * @return Probe class that should be triggered.
	 */
	Class<? extends EventProbe<?, ?, ?>> value();

	@Retention(RUNTIME)
	@Target(TYPE)
	@Documented
	public @interface ProbeTriggers {
		ProbeTrigger[] value();
	}

}
