package org.palladiosimulator.analyzer.slingshot.simulation.core.extensions;

import java.lang.reflect.Method;

public interface Interceptor {
	
	void preIntercept(final Object extension, final Method m, final Object[] args);
	
	void postIntercept(final Object result, final Object self, final Method m, final Object[] args);

}
