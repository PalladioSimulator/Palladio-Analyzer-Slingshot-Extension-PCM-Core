package org.palladiosimulator.analyzer.slingshot.simulation.driver.behavior.interceptors;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import org.palladiosimulator.analyzer.slingshot.simulation.events.AbstractGenericEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.Reified;

/**
 * Pre-intercepts every method that is of type {@link AbstractGenericEvent}.
 * <p>
 * Note that the this interceptor will not prevent executing the handler if the
 * parameter of the generic event was not annotated with {@link Reified}.
 * 
 * @author Julijan Katic
 *
 */
public class CheckGenericEventInterceptor extends AbstractInterceptor {

	@Override
	public void preIntercept(final Object extension, final Method method, final Object[] args) {
		assert args.length == 1 && method.getParameterCount() == 1;
		final Parameter eventParameter = method.getParameters()[0];
		final Object actualEvent = args[0];

		if (actualEvent instanceof AbstractGenericEvent<?, ?> && eventParameter.isAnnotationPresent(Reified.class)) {
			final Reified reifiedAnnotation = eventParameter.getAnnotation(Reified.class);
			final AbstractGenericEvent<?, ?> genericEvent = (AbstractGenericEvent<?, ?>) actualEvent;

			/* Abort if reified type and actual event do not match. */
			if (!reifiedAnnotation.value().equals(genericEvent.getGenericType())) {
				throw new EventHandlerAbortedException("Generic type does not match.");
			}
		}
	}

}
