package org.palladiosimulator.analyzer.slingshot.behavior.generalsimulationconfiguration;

import de.uka.ipd.sdq.probfunction.math.IProbabilityFunctionFactory;
import de.uka.ipd.sdq.probfunction.math.impl.ProbabilityFunctionFactoryImpl;
import de.uka.ipd.sdq.simucomframework.variables.cache.StoExCache;

import org.palladiosimulator.analyzer.slingshot.simulation.core.events.SimulationStarted;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.SimulationBehaviorExtension;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.OnEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.results.ResultEvent;

import com.google.common.eventbus.Subscribe;

import org.apache.log4j.Logger;

/**
 * This behavior is used in order to have a general implementation for configuraring
 * the simulator beforehand. For instance, this class initializes the {@link StoEx} cache
 * on the simulation start.
 * 
 * @author Julijan Katic
 * @version 1.0
 */
@OnEvent(when = SimulationStarted.class, then = {})
public class GeneralSimulationConfigurationBehavior implements SimulationBehaviorExtension {
	
	private final static Logger LOGGER = Logger.getLogger(GeneralSimulationConfigurationBehavior.class);
	
	/**
	 * Initializes the simulation for each component.
	 * 
	 * @return an empty set.
	 */
	@Subscribe
	public ResultEvent<?> onSimulationStarted(final SimulationStarted simulationStarted) {
		/* Initialize ProbFunction and StoExCache, otherwise StackContext won't work */
		final IProbabilityFunctionFactory probabilityFunctionFactory = ProbabilityFunctionFactoryImpl.getInstance();
		StoExCache.initialiseStoExCache(probabilityFunctionFactory);
		LOGGER.info("Initialized probability function");
		
		return ResultEvent.empty();
	}
	
}
