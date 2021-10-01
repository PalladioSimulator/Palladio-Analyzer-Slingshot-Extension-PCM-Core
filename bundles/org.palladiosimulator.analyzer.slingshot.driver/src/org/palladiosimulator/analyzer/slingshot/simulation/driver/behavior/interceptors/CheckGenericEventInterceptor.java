package org.palladiosimulator.analyzer.slingshot.simulation.driver.behavior.interceptors;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Optional;

import org.apache.log4j.Logger;
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

	private static final Logger LOGGER = Logger.getLogger(CheckGenericEventInterceptor.class);

	@Override
	public void preIntercept(final Object extension, final Method method, final Object[] args) {
		assert args.length == 1 && method.getParameterCount() == 1;
		final Parameter eventParameter = this.findParamaterFromSuperClass(extension, method).get();
		final Object actualEvent = args[0];

		if (actualEvent instanceof AbstractGenericEvent<?, ?> && eventParameter.isAnnotationPresent(Reified.class)) {
			LOGGER.info("Check generic type of " + method.getName() + " and parameter " + eventParameter.getName());
			final Reified reifiedAnnotation = eventParameter.getAnnotation(Reified.class);
			final AbstractGenericEvent<?, ?> genericEvent = (AbstractGenericEvent<?, ?>) actualEvent;
			LOGGER.info("Types: " + reifiedAnnotation.value() + " and " + genericEvent.getGenericType().getName());
			/* Abort if reified type and actual event do not match. */
			if (!reifiedAnnotation.value().isAssignableFrom(genericEvent.getGenericType())) {
				throw new EventHandlerAbortedException("Generic type does not match: " +
						reifiedAnnotation.value().getName() + " and " + genericEvent.getGenericType().getName());
			} else {
				LOGGER.info("Same generic event!");
			}
		}
	}

	private Optional<Parameter> findParamaterFromSuperClass(final Object extension, final Method method) {
		return Arrays.stream(extension.getClass().getSuperclass().getMethods())
				.filter(m -> m.getName().equals(method.getName()) &&
						Arrays.equals(m.getParameterTypes(), method.getParameterTypes()))
				.map(m -> m.getParameters()[0])
				.findAny();

	}

}
