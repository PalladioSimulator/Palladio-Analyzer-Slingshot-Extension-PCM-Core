package org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.decorators;

import java.util.List;

import org.palladiosimulator.analyzer.slingshot.simulation.core.SimulationBehaviourExtension;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.Interceptor;

public interface DecoratedSimulationBehaviorProvider {
	
	SimulationBehaviourExtension decorateSimulationBehaviorWithInterceptors(List<Interceptor> interceptors) throws Exception;
	
}
