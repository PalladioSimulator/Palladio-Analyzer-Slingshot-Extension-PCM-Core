package org.palladiosimulator.analyzer.slingshot.simulation.driver.behavior.interceptors;

import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.simulation.api.SimulationScheduling;
import org.palladiosimulator.analyzer.slingshot.simulation.core.events.HandlerInterrupted;

/**
 * A MethodHandler to add interceptors to the extensions, so that the behavior
 * of extensions undergoes through the workflow of interceptors.
 * 
 * @author Floriment Klinaku
 *
 */
public class ExtensionMethodHandlerWithInterceptors implements MethodInterceptor {

	private static final Logger LOGGER = Logger.getLogger(ExtensionMethodHandlerWithInterceptors.class);

	/**
	 * an ordered list of interceptors where for each preIntercept will be invoked
	 * before the intercepted method and postIntercept after the execution of the
	 * intercepted method.
	 */
	private final List<Interceptor> myInterceptors;

	/** Used for creating a {@link HandlerInterrupted} event. */
	private final SimulationScheduling scheduling;

	public ExtensionMethodHandlerWithInterceptors(final List<Interceptor> interceptors,
			final SimulationScheduling scheduling) {
		this.myInterceptors = interceptors;
		this.scheduling = scheduling;
	}

	@Override
	public Object invoke(final MethodInvocation invocation) throws Throwable {

		try {
			LOGGER.info(String.format("+++ Intercepting the extension method: %s#%s",
					invocation.getThis().getClass().getName(), invocation.getMethod().getName()));

			/* Pre-intercept everything. */
			for (final Interceptor interceptor : this.myInterceptors) {
				interceptor.preIntercept(invocation.getThis(), invocation.getMethod(), invocation.getArguments());
			}

			/* Call the actual event handler. */
			final Object result = invocation.proceed();

			/* Post-intercept everything. */
			for (final Interceptor interceptor : this.myInterceptors) {
				interceptor.postIntercept(invocation.getThis(), invocation.getMethod(), invocation.getArguments(),
						result);
			}

			LOGGER.info("+++ Interception Ended +++");

			return result;
		} catch (final EventHandlerAbortedException exception) {
			LOGGER.warn(String.format("The method interception was explicitly aborted: %s", exception.getMessage()));
		} catch (final Exception e) {
			LOGGER.error("An exception was thrown during an event handling", e);
			// In this case, schedule a HandlerInterrupted.
			this.scheduling.scheduleForSimulation(new HandlerInterrupted(e));
		}

		return null;
	}

}
