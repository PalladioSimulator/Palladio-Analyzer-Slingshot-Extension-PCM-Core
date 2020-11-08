package org.palladiosimulator.analyzer.slingshot.simulation.extensions.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.SimulationBehaviorExtension;

/**
 * Declares a certain class as a behavior extension to the Slingshot system. The
 * class must implement {@link SimulationBehaviorExtension}.
 * 
 * @author Julijan Katic
 */
@Documented
@Retention(CLASS)
@Target(TYPE)
public @interface BehaviorExtension {

}
