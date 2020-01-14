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
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.decorators.DecoratedUsageSimulationProvider;
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
	
	private final static Logger LOGGER = Logger.getLogger(SimulationFactory.class);

	
	public static Simulation createSimulation() throws Exception {
		// The first SimulationBehaviourExtension
		UsageModelRepositoryImpl usageModelRepository = new UsageModelRepositoryImpl();
		SimulatedUserProvider simulatedUserProvider = new SimulatedUserProvider();
//		SimulationBehaviourExtension usageSimulation = new UsageSimulationImpl(usageModelRepository, simulatedUserProvider);	
		
		
		// The Core
		Dispatcher eventDispatcher = new Dispatcher();
		SimulationEngine simEngine = new SimulationEngineMock(eventDispatcher);
		
		// Extensions
		DecoratedUsageSimulationProvider decoratedUsageSimulationProvider = new DecoratedUsageSimulationProvider(usageModelRepository, simulatedUserProvider);


		var simulationBehaviorExtensions = new ArrayList<SimulationBehaviourExtension>();
		
		// Simulation Driver
		SimulationDriver simulationDriver =  new SimulationDriver(simEngine, simulationBehaviorExtensions, decoratedUsageSimulationProvider);
		
		
		
	
				
		return simulationDriver;
	}

}
