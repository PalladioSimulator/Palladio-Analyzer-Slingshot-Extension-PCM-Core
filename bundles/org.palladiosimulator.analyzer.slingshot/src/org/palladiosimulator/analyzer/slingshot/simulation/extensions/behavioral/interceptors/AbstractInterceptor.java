package org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.interceptors;

import java.lang.reflect.Method;

/**
 * A helper class that already provides an implementation of {@link Interceptor} with default behaviour, that is,
 * it does nothing. An implementation can therefore extend this class instead of directly implementing the {@link Interceptor}
 * so that not both methods need to be declared.
 * 
 * @author Julijan Katic
 */
public abstract class AbstractInterceptor implements Interceptor {
	
	@Override
	public void preIntercept(final Object extension, final Method method, final Object[] args) {
		
	}

	@Override
	public void postIntercept(final Object extension, final Method method, final Object[] args, final Object result) {
		
	}

}
