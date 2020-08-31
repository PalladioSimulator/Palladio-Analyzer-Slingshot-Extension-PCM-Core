package org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.behavioural;

import java.lang.reflect.Method;

/**
 * This interface describes interceptors especially for extension methods at runtime. It provides
 * methods to handle the specified behaviour before and/or after the actual method call.
 * 
 * @author Julijan Katic
 * @see AbstractInterceptor
 */
public interface Interceptor {
	
	/**
	 * Intercepts a method BEFORE it is called. 
	 * 
	 * @param extension The instance of that class in which the method is defined. 
	 * @param m The reflected method itself.
	 * @param args The instances of the arguments for the method m.
	 */
	void preIntercept(final Object extension, final Method m, final Object[] args);
	
	/**
	 * Intercepts a method AFTER it is called.
	 * 
	 * @param extension The instance of that class in which the method is defined.
	 * @param m The reflected method itself.
	 * @param args The instances of the arguments for the method m.
	 * @param result The instance that results from the method invocation. It has the type as specified in the method header.
	 */
	void postIntercept(final Object extension, final Method m, final Object[] args, final Object result);

}
