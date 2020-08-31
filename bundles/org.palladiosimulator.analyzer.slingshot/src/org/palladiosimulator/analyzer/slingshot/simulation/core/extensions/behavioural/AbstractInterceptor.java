package org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.behavioural;

import java.lang.reflect.Method;

/**
 * A helper class that implements an {@link Interceptor} interface with default behavior, that is,
 * it does nothing.
 * 
 * @author Julijan Katic
 */
public abstract class AbstractInterceptor implements Interceptor{

	@Override
	public void preIntercept(final Object extension, final Method m, final Object[] args) {
		
	}

	@Override
	public void postIntercept(final Object extension, final Method m, final Object[] args, final Object result) {
		
	}

}
