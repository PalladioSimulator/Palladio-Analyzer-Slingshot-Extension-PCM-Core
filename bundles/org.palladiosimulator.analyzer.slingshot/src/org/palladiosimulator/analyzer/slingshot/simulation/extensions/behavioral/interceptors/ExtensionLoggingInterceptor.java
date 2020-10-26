package org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.interceptors;
import java.lang.reflect.Method;

import org.apache.log4j.Logger;

/**
 * Helper class for debuging purposes: Prints that a method is intercepted
 * before and after the invocation.
 * 
 * @author Julijan Katic
 */
public class ExtensionLoggingInterceptor extends AbstractInterceptor {

	
	private final Logger LOGGER = Logger.getLogger(ExtensionLoggingInterceptor.class);

	public ExtensionLoggingInterceptor() {
		
	}

	@Override
	public void preIntercept(final Object extension, final Method m, final Object[] args) {
		
		LOGGER.info("Pre-intercept of the method: "+m.getName());

		
	}
	
	@Override
	public void postIntercept(final Object extension, final Method m, final Object[] args,final Object result) {
		LOGGER.info("Post-intercept of the method: " + m.getName());
	}


	

}
