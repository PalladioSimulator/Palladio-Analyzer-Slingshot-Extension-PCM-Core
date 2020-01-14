package org.palladiosimulator.analyzer.slingshot.simulation.core.extensions;

import java.lang.reflect.Method;
import java.util.List;

import org.palladiosimulator.analyzer.slingshot.simulation.core.SimulationBehaviourExtension;

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
	private List<Interceptor> myInterceptors;
	
	public ExtensionMethodHandlerWithInterceptors(List<Interceptor> interceptors) {
		myInterceptors = interceptors;
	}

	@Override
    public Object invoke(Object self, Method method, Method proceed, Object[] args) throws Throwable {
	
		for (Interceptor interceptor : myInterceptors) {
			interceptor.preIntercept(self, method, args);
		}
	
		Object result = proceed.invoke(self, args);  // execute the original method.
		
		for (Interceptor interceptor : myInterceptors) {
			interceptor.postIntercept(result, self, method, args);
		}
		
		return result;
    }

}
