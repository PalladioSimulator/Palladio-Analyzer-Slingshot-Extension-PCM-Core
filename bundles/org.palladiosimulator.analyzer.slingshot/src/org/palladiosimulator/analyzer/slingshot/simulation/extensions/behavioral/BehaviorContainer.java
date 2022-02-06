package org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;

/**
 * This class is the container for the {@code SimulationBehaviorExtension}s. It
 * is responsible for injecting the modules and loading all necessary providers.
 * 
 * @author Julijan Katic
 */
public final class BehaviorContainer extends AbstractModule {

	private static final Logger LOGGER = Logger.getLogger(BehaviorContainer.class);

	private final SimulationBehaviorExtensionLoader loader;
	private List<Class<? extends SimulationBehaviorExtension>> clazzExtensions = null;
	private final List<SimulationBehaviorExtension> extensions;

	public BehaviorContainer() {
		this.loader = new SimulationBehaviorExtensionLoader();
		this.extensions = new ArrayList<>();
	}

	// @Override
	public void loadExtensions(final Injector injector) {
		if (this.clazzExtensions == null) {
			LOGGER.error("The clazzes list is null!");
			return;
		}

		for (final Class<? extends SimulationBehaviorExtension> clazz : this.clazzExtensions) {
			this.extensions.add(injector.getInstance(clazz));
		}
	}

	@Override
	public void configure() {

		this.clazzExtensions = this.loader.getAllProviders();

		/*
		 * Explicitly bind them in order to allow the (possible) child
		 * injector to intercept it.
		 */
		this.clazzExtensions.forEach(this::bind);
	}

	// @Override
	public List<SimulationBehaviorExtension> getExtensions() {
		return Collections.unmodifiableList(this.extensions);
	}

}
