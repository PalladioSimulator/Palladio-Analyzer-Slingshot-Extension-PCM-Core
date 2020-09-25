package org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.decorators;

import java.util.List;

import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.SimulationBehaviorExtension;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.interceptor.Interceptor;

/**
 * Interface for providing new extensions to the system.
 * 
 * @author Julijan Katic
 */
public interface DecoratedSimulationBehaviorProvider {

	/**
	 * This method should return a new {@link SimulationBehaviorExtension} that
	 * extends the simulation behaviour. Also, this should provide a non-null list
	 * of interceptors that will intercept the extension at some point in time.
	 * 
	 * @param interceptors a non-null list of interceptors. It may be empty.
	 * @return a new extension to the system.
	 * @throws Exception an arbitrary exception in the implementation. Should be
	 *                   further specified when implementing the code.
	 */
	SimulationBehaviorExtension decorateSimulationBehaviorWithInterceptors(final List<Interceptor> interceptors)
			throws Exception;

}
