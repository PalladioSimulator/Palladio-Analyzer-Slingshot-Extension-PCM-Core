package org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.behavioural.decorators;

import java.lang.reflect.Method;
import java.util.List;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.behavioural.SimulationBehaviourExtension;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.behavioural.interceptors.ExtensionMethodHandlerWithInterceptors;
import org.palladiosimulator.analyzer.slingshot.simulation.interceptor.Interceptor;

import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.Proxy;
import javassist.util.proxy.ProxyFactory;

/**
 * This abstract class provides a way to extend the behaviour extension point of
 * the system. It already handles the interception of the extension by using the
 * {@link Interceptor}-Api, and pre- and post-intercepts each method of a class
 * whose name starts with "on". These methods are typically handling the events
 * of the system.
 * 
 * Furthermore, in order to initialize the extension, abstract methods are
 * defined that provides the information of a class such as the constructor
 * argument types, the class itself and the constructor instances.
 * 
 * @author Julijan Katic
 */
public abstract class AbstractDecoratedSimulationBehaviorProvider implements DecoratedSimulationBehaviorProvider {

	private static final Logger LOGGER = Logger.getLogger(AbstractDecoratedSimulationBehaviorProvider.class);

	@Override
	public SimulationBehaviourExtension decorateSimulationBehaviorWithInterceptors(final List<Interceptor> interceptors)
			throws Exception {

		final ProxyFactory proxyFactory = new ProxyFactory();
		proxyFactory.setSuperclass(getToBeDecoratedClazz());

		proxyFactory.setFilter(new MethodFilter() {
			@Override
			public boolean isHandled(final Method m) {
				boolean answer = false;

				// TODO:: Think how to filter the methods that we are particulary checking
				if (m.getName().startsWith("on")) {
					answer = true;
				}

				return answer;

			}
		});

		LOGGER.debug("The to be decorated class: " + this.getToBeDecoratedClazz().getSimpleName());

		final Class<?> simulationBehaviourProxyClazz = proxyFactory.createClass();

		final SimulationBehaviourExtension decoratedUsageSimulation = (SimulationBehaviourExtension) simulationBehaviourProxyClazz
				.getConstructor(getConstructorArgumentsClazzes()).newInstance(getConstructorInstances());

		// TODO:: Have a look at this Proxy cast
		((Proxy) decoratedUsageSimulation).setHandler(new ExtensionMethodHandlerWithInterceptors(interceptors));

		return decoratedUsageSimulation;

	}

	/**
	 * This method should return the class that extends the system's behaviour.
	 */
	protected abstract Class<?> getToBeDecoratedClazz();

	/**
	 * This method should return an array of classes that are needed to construct
	 * the class from {@link #getToBeDecoratedClazz()}.
	 */
	protected abstract Class<?>[] getConstructorArgumentsClazzes();

	/**
	 * This method should return an array of instances for the constructor in order
	 * to construct the class returned by {@link #getToBeDecoratedClazz()}.
	 */
	protected abstract Object[] getConstructorInstances();

}
