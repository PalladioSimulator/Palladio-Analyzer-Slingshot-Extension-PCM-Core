package org.palladiosimulator.analyzer.slingshot.simulation.api;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.repositories.UsageModelRepository;
import org.palladiosimulator.analyzer.slingshot.repositories.impl.UsageModelRepositoryImpl;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.ExtensionMethodHandlerWithInterceptors;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.ContractEnforcementInterceptor;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.ExtensionLoggingInterceptor;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.Interceptor;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.SchedulingInterceptor;
import org.palladiosimulator.analyzer.slingshot.simulation.core.SimulationBehaviourExtension;
import org.palladiosimulator.analyzer.slingshot.simulation.core.SimulationDriver;
import org.palladiosimulator.analyzer.slingshot.simulation.engine.SimulationEngine;
import org.palladiosimulator.analyzer.slingshot.simulation.engine.SimulationEngineMock;
import org.palladiosimulator.analyzer.slingshot.simulation.events.Dispatcher;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.SimulatedUserProvider;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.UsageSimulationImpl;
import org.palladiosimulator.pcm.usagemodel.UsageModel;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.Proxy;
import javassist.util.proxy.ProxyFactory;

public class SimulationFactory {
	
	public static Simulation createSimulation() {
		// The first SimulationBehaviourExtension
		UsageModelRepositoryImpl usageModelRepository = new UsageModelRepositoryImpl();
		SimulatedUserProvider simulatedUserProvider = new SimulatedUserProvider();
//		SimulationBehaviourExtension usageSimulation = new UsageSimulationImpl(usageModelRepository, simulatedUserProvider);	
		
		
		ProxyFactory f = new ProxyFactory();    
	    f.setSuperclass(UsageSimulationImpl.class);
	    
	    Class c = f.createClass();
	    
	    
		SimulationBehaviourExtension myDecoratedBehavior = null;
		try {
			myDecoratedBehavior = (SimulationBehaviourExtension) c.getConstructor(
					new Class[] {UsageModelRepository.class,SimulatedUserProvider.class}).newInstance(new Object[] {usageModelRepository,simulatedUserProvider});
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			
			throw (RuntimeException) e.getCause();
			
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		// The Core
		Dispatcher eventDispatcher = new Dispatcher();
		SimulationEngine simEngine = new SimulationEngineMock(eventDispatcher);
		
		// Add additional constructor which has a list of extension.
		var simulationBehaviorExtensions = new ArrayList<SimulationBehaviourExtension>();
		simulationBehaviorExtensions.add(myDecoratedBehavior);
		
		
		SimulationDriver simulationDriver =  new SimulationDriver(simEngine,simulationBehaviorExtensions);
	
		ExtensionLoggingInterceptor myLoggingInterceptor = new ExtensionLoggingInterceptor();
		SchedulingInterceptor schedulingInterceptor = new SchedulingInterceptor(simulationDriver);
		ContractEnforcementInterceptor contract = new ContractEnforcementInterceptor();
		
		List<Interceptor> interceptors = List.of(contract, myLoggingInterceptor, schedulingInterceptor);
				
		((Proxy)myDecoratedBehavior).setHandler(new ExtensionMethodHandlerWithInterceptors(interceptors));

				
		return simulationDriver;
	}

}
