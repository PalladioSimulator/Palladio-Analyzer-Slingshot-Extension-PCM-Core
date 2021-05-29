package org.palladiosimulator.analyzer.slingshot.monitor.calculator;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.palladiosimulator.analyzer.slingshot.monitor.probe.DESEventProbe;
import org.palladiosimulator.probeframework.calculator.Calculator;

/**
 * Specifies that a method can provide a Calculator object based on two probe
 * instances. The probe instances can be given by the monitor, and thus should
 * be parameters of the method. The return type should be a (sub-)type of
 * {@link Calculator}. The probes themselves should be a (sub-)type of
 * {@link DESEventProbe}, which keeps track of the event that spawns a probe.
 * 
 * @author Julijan Katic
 */
@Documented
@Retention(RUNTIME)
@Target(METHOD)
public @interface CalculatorProvider {

}
