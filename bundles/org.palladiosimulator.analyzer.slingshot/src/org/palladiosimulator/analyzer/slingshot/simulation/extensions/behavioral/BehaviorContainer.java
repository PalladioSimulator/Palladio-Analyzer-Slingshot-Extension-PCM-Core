package org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.palladiosimulator.analyzer.slingshot.util.extensionpoint.ExtensionInstancesContainer;

import com.google.inject.Injector;

public class BehaviorContainer implements ExtensionInstancesContainer<SimulationBehaviorExtension> {

	private static BehaviorContainer INSTANCE = null;

	private final SimulationBehaviorExtensionLoader loader;
	private List<Class<? extends SimulationBehaviorExtension>> clazzExtensions = null;
	private final List<SimulationBehaviorExtension> extensions;

	private BehaviorContainer() {
		this.loader = new SimulationBehaviorExtensionLoader();
		this.extensions = new ArrayList<>();
	}

	@Override
	public void loadExtensions(final Injector injector) {
		this.clazzExtensions = loader.getAllProviders();

		for (final Class<? extends SimulationBehaviorExtension> clazz : clazzExtensions) {
			this.extensions.add(injector.getInstance(clazz));
		}
	}

	@Override
	public List<SimulationBehaviorExtension> getExtensions() {

		return Collections.unmodifiableList(extensions);
	}

	public static BehaviorContainer getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new BehaviorContainer();
		}

		return INSTANCE;
	}
}
