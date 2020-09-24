package org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.behavioural.interceptors;

import java.lang.reflect.Method;
import java.util.List;

import org.palladiosimulator.analyzer.slingshot.simulation.interceptor.Interceptor;

import javassist.util.proxy.MethodHandler;


/**
 * A MethodHandler to add interceptors to the extensions, so that the behavior 
 * of extensions undergoes through the workflow of interceptors.
 * 
 * @author Floriment Klinaku
 *
 */
public class ExtensionMethodHandlerWithInterceptors implements MethodHandler {
	/**
	 * an ordered list of interceptors where for each preIntercept will be invoked before the intercepted method
	 * and postIntercept after the execution of the intercepted method.
	 * TODO:: A list of preInterceptors and postInterceptors would make more sense
	 */
	private final List<Interceptor> myInterceptors;
	
	public ExtensionMethodHandlerWithInterceptors(final List<Interceptor> interceptors) {
		myInterceptors = interceptors;
	}

	@Override
    public Object invoke(final Object extension, final Method method, final Method proceed, final Object[] args) throws Throwable {
	
		for (final Interceptor interceptor : myInterceptors) {
			interceptor.preIntercept(extension, method, args);
		}
	
		final Object result = proceed.invoke(extension, args);  // execute the original method.
		
		for (final Interceptor interceptor : myInterceptors) {
			interceptor.postIntercept(extension, method, args, result);
		}
		
		return result;
    }

}
