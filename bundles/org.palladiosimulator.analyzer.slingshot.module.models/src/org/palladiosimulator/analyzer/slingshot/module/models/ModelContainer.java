package org.palladiosimulator.analyzer.slingshot.module.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.inject.Module;

public class ModelContainer {

	private final List<Module> modelProviders;

	public ModelContainer() {
		this.modelProviders = new ArrayList<>();
	}

	public void addModule(final Module module) {
		this.modelProviders.add(module);
	}

	public void addModules(final Collection<Module> modules) {
		this.modelProviders.addAll(modules);
	}

	public void addModules(final List<ModelProvider> modules) {
		this.modelProviders.addAll(modules);
	}

	public void addModules(final Module... modules) {
		this.addModules(modules);
	}

	protected List<Module> getModelProviders() {
		return modelProviders;
	}
}
