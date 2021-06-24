package org.palladiosimulator.analyzer.slingshot.simulation.driver.behavior.interceptors;

import java.lang.reflect.Method;
import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.log4j.Logger;

import javassist.util.proxy.MethodHandler;

/**
 * A MethodHandler to add interceptors to the extensions, so that the behavior
 * of extensions undergoes through the workflow of interceptors.
 * 
 * @author Floriment Klinaku
 *
 */
public class ExtensionMethodHandlerWithInterceptors implements MethodHandler, MethodInterceptor {

	private static final Logger LOGGER = Logger.getLogger(ExtensionMethodHandlerWithInterceptors.class);

	/**
	 * an ordered list of interceptors where for each preIntercept will be invoked
	 * before the intercepted method and postIntercept after the execution of the
	 * intercepted method.
	 */
	private final List<Interceptor> myInterceptors;

	public ExtensionMethodHandlerWithInterceptors(final List<Interceptor> interceptors) {
		myInterceptors = interceptors;
	}

	@Override
	public Object invoke(final Object extension, final Method method, final Method proceed, final Object[] args)
	        throws Throwable {

		LOGGER.info(String.format("+++ Intercepting the extension method: %s#%s +++",
		        extension.getClass().getSimpleName(), method.getName()));

		for (final Interceptor interceptor : myInterceptors) {
			interceptor.preIntercept(extension, method, args);
		}

		final Object result = proceed.invoke(extension, args); // execute the original method.

		for (final Interceptor interceptor : myInterceptors) {
			interceptor.postIntercept(extension, method, args, result);
		}

		LOGGER.info("+++ Interception Ended +++");

		return result;
	}

	@Override
	public Object invoke(final MethodInvocation invocation) throws Throwable {
		for (final Interceptor interceptor : myInterceptors) {
			interceptor.preIntercept(invocation.getThis(), invocation.getMethod(), invocation.getArguments());
		}

		final Object result = invocation.proceed();

		for (final Interceptor interceptor : myInterceptors) {
			interceptor.postIntercept(invocation.getThis(), invocation.getMethod(), invocation.getArguments(), result);
		}

		return result;
	}

}
