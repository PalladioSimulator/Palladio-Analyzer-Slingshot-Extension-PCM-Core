package org.palladiosimulator.analyzer.slingshot.simulation.core.extensions;

import java.lang.reflect.Method;

public abstract class AbstractInterceptor implements Interceptor{

	@Override
	public void preIntercept(Object extension, Method m, Object[] args) {
		
	}

	@Override
	public void postIntercept(Object extension, Method m, Object[] args, Object result) {
		
	}

}
