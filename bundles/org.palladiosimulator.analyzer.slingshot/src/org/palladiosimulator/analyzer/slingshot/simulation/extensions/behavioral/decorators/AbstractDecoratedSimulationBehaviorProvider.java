package org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.decorators;

import java.lang.reflect.Method;
import java.util.List;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.SimulationBehaviorExtension;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.EventMethod;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.interceptors.ExtensionMethodHandlerWithInterceptors;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.interceptor.Interceptor;

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
	public SimulationBehaviorExtension decorateSimulationBehaviorWithInterceptors(final List<Interceptor> interceptors)
			throws Exception {

		final ProxyFactory proxyFactory = new ProxyFactory();
		proxyFactory.setSuperclass(getToBeDecoratedClazz());

		proxyFactory.setFilter(this::isBehaviorExtensionHandler);

		LOGGER.debug("The to be decorated class: " + this.getToBeDecoratedClazz().getSimpleName());

		final Class<?> simulationBehaviourProxyClazz = proxyFactory.createClass();

		final SimulationBehaviorExtension decoratedUsageSimulation = (SimulationBehaviorExtension) simulationBehaviourProxyClazz
				.getConstructor(getConstructorArgumentsClazzes()).newInstance(getConstructorInstances());

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
	 * 
	 * @return an empty array on default.
	 */
	protected Class<?>[] getConstructorArgumentsClazzes() {
		return new Class<?>[] {};
	}

	/**
	 * This method should return an array of instances for the constructor in order
	 * to construct the class returned by {@link #getToBeDecoratedClazz()}.
	 * 
	 * @return an empty array on default.
	 */
	protected Object[] getConstructorInstances() {
		return new Object[] {};
	}

	/**
	 * Helper method to check whether the method is an event handler method.
	 * 
	 * @param method the method to check.
	 * @return true iff the method starts with "on" or has an {@link EventMethod}
	 *         annotation.
	 */
	private boolean isBehaviorExtensionHandler(final Method method) {
		final boolean isTheRightMethod = method.getName().startsWith("on")
				|| method.getAnnotation(EventMethod.class) != null;
		LOGGER.debug(String.format("Method name: %s (%s)", method.getName(), isTheRightMethod));
		return isTheRightMethod;
	}
}
