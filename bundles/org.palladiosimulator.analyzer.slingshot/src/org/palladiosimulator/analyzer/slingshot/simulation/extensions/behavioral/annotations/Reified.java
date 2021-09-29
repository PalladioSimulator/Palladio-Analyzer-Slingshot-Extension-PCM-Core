package org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotate an event-handler parameter with this whose type is generic.
 * <p>
 * In order to preserve generic types at runtime, the {@code Reified} type is
 * needed. The event-dispatcher can then use this information to further
 * determine which event-handler to use.
 * <p>
 * For example, given the following event-handlers with a generic event
 * {@code GenericEvent<T>},
 * 
 * <pre>
 * {@code
 * public ResultEvent<?> onGenericEventA(final GenericEvent<A> genericEventA)
 * 	 
 * public ResultEvent<?> onGenericEventB(final GenericEvent<B> genericEventB)
 * }
 * </pre>
 * 
 * when dispatching an event {@code GenericEvent<A>}, then the JVM cannot
 * distinguish between {@code A} and {@code B}, since these type information are
 * erased during runtime. This means that BOTH event handlers would be
 * activated!
 * <p>
 * However, if the {@code @Reified} annotation is given, then the
 * event-dispatcher can further distinguish between them:
 * 
 * {@code onGenericEventA(@Reified(A.class) final GenericEvent<A> genericEventA)}
 * 
 * @author Julijan Katic
 */
@Documented
@Retention(RUNTIME)
@Target(PARAMETER)
public @interface Reified {

	Class<?> value();

}
