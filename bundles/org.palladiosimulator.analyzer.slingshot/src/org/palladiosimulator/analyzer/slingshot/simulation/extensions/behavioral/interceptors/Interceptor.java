package org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.interceptors;

import java.lang.reflect.Method;

/**
 * Describes interceptors, especially for event handler methods, that will be
 * executed before and after the method invocation.
 * 
 * @author Julijan Katic
 */
public interface Interceptor {

	/**
	 * Intercepts the method invocation and is executed before-handly.
	 * 
	 * @param extension The instance of that class in which the method lies.
	 * @param method    The method that will be intercepted.
	 * @param args      The arguments that are passed onto the method.
	 */
	void preIntercept(final Object extension, final Method method, final Object[] args);

	/**
	 * Intercepts the method invocation and is executed afterwards.
	 * 
	 * @param extension The instance of that class in which the method lies.
	 * @param method    The method that will be intercepted.
	 * @param args      The arguments that are passed onto the method.
	 * @param result    The value returned by the method. If the type of the method
	 *                  is void, this should then be null.
	 */
	void postIntercept(final Object extension, final Method method, final Object[] args, final Object result);

}
