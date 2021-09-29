package org.palladiosimulator.analyzer.slingshot.simulation.driver.behavior.interceptors;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.simulation.api.SimulationScheduling;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.EventMethod;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matchers;

public final class DefaultInterceptorModule extends AbstractModule {

	private static final Logger LOGGER = Logger.getLogger(DefaultInterceptorModule.class);

	private final List<Interceptor> interceptors;
	private final SimulationScheduling scheduling;

	public DefaultInterceptorModule(final SimulationScheduling scheduler) {
		final ExtensionLoggingInterceptor loggingInterceptor = new ExtensionLoggingInterceptor();
		final SchedulingInterceptor schedulingInterceptor = new SchedulingInterceptor(scheduler);
		final SimulationExtensionOnEventContractEnforcementInterceptor contractInterceptor = new SimulationExtensionOnEventContractEnforcementInterceptor();
		// final EventMonitoringInterceptor eventMonitoringInterceptor = new
		// EventMonitoringInterceptor(eventGraph);
		final CheckGenericEventInterceptor checkGenericEventInterceptor = new CheckGenericEventInterceptor();

		this.interceptors = List.of(checkGenericEventInterceptor, loggingInterceptor, schedulingInterceptor,
				contractInterceptor);
		this.scheduling = scheduler;
	}

	@Override
	protected void configure() {
		this.bindInterceptor(
				Matchers.any(),
				new ExtensionMethodMatcher(),
				new ExtensionMethodHandlerWithInterceptors(this.interceptors, this.scheduling));
	}

	/**
	 * Matcher that returns true if the method either starts with "on" or is
	 * annotated with {@link EventMethod} Annotation.
	 */
	class ExtensionMethodMatcher extends AbstractMatcher<Method> implements Serializable {

		private static final long serialVersionUID = 1L;

		@Override
		public boolean matches(final Method method) {
			final boolean isTheRightMethod = method.getName().startsWith("on")
					|| method.getAnnotation(EventMethod.class) != null;
			LOGGER.debug(String.format("Method name: %s (%s)", method.getName(), isTheRightMethod));
			return isTheRightMethod;
		}

	}

}
