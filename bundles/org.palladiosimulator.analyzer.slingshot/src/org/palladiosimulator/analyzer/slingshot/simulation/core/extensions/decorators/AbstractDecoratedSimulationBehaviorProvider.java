package org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.decorators;

import java.lang.reflect.Method;
import java.util.List;

import org.palladiosimulator.analyzer.slingshot.simulation.core.SimulationBehaviourExtension;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.ExtensionMethodHandlerWithInterceptors;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.Interceptor;

import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.Proxy;
import javassist.util.proxy.ProxyFactory;

public abstract class AbstractDecoratedSimulationBehaviorProvider implements DecoratedSimulationBehaviorProvider {

	@Override
	public SimulationBehaviourExtension decorateSimulationBehaviorWithInterceptors(List<Interceptor> interceptors)
			throws Exception {
		
		ProxyFactory proxyFactory = new ProxyFactory();    
	    proxyFactory.setSuperclass(getToBeDecoratedClazz());
	    
	    
	    proxyFactory.setFilter(new MethodFilter() {
	        public boolean isHandled(Method m) {
	        	boolean answer = false;
	        	
	        	//TODO:: Think how to filter the methods that we are particulary checking
        		if(m.getName().startsWith("on")) {
					answer = true;
				}
		   	
	        	return answer;
	            
	        }
	    });
	    
	    Class<?> simulationBehaviourProxyClazz = proxyFactory.createClass();
	    
		SimulationBehaviourExtension decoratedUsageSimulation = (SimulationBehaviourExtension) simulationBehaviourProxyClazz.getConstructor(getConstructorArgumentsClazzes()).newInstance(getConstructorInstances());
			
		//TODO:: Have a look at this Proxy cast
		((Proxy)decoratedUsageSimulation).setHandler(new ExtensionMethodHandlerWithInterceptors(interceptors));
		
		return decoratedUsageSimulation;

	}
	
	abstract Class<?> getToBeDecoratedClazz();
	
	abstract Class<?>[] getConstructorArgumentsClazzes();
	
	abstract Object[] getConstructorInstances();
	
	

}
