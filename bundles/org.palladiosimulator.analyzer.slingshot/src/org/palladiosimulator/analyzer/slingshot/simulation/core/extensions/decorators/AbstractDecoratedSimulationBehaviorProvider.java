package org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.decorators;

import java.util.List;

import org.palladiosimulator.analyzer.slingshot.simulation.core.SimulationBehaviourExtension;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.ExtensionMethodHandlerWithInterceptors;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.Interceptor;

import javassist.util.proxy.Proxy;
import javassist.util.proxy.ProxyFactory;

public abstract class AbstractDecoratedSimulationBehaviorProvider implements DecoratedSimulationBehaviorProvider {

	@Override
	public SimulationBehaviourExtension decorateSimulationBehaviorWithInterceptors(List<Interceptor> interceptors)
			throws Exception {
		
		ProxyFactory proxyFactory = new ProxyFactory();    
	    proxyFactory.setSuperclass(getToBeDecoratedClazz());
	    
	    Class<?> simulationBehaviourProxyClazz = proxyFactory.createClass();
	    
		SimulationBehaviourExtension decoratedUsageSimulation = (SimulationBehaviourExtension) simulationBehaviourProxyClazz.getConstructor(getConstructorArgumentsClazzes()).newInstance(getConstructorInstances());
				
		((Proxy)decoratedUsageSimulation).setHandler(new ExtensionMethodHandlerWithInterceptors(interceptors));
		
		return decoratedUsageSimulation;

	}
	
	abstract Class<?> getToBeDecoratedClazz();
	
	abstract Class<?>[] getConstructorArgumentsClazzes();
	
	abstract Object[] getConstructorInstances();
	
	

}
