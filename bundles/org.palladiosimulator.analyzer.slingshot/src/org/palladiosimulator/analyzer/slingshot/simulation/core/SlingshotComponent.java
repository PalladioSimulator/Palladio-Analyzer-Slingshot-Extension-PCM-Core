package org.palladiosimulator.analyzer.slingshot.simulation.core;

import java.util.ArrayList;
import java.util.List;

import org.palladiosimulator.analyzer.slingshot.simulation.api.EventDispatcher;
import org.palladiosimulator.analyzer.slingshot.simulation.api.Simulation;
import org.palladiosimulator.analyzer.slingshot.simulation.api.SimulationEngine;
import org.palladiosimulator.analyzer.slingshot.simulation.api.SimulationMonitoring;
import org.palladiosimulator.analyzer.slingshot.simulation.api.SimulationScheduling;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * The Slingshot model contains all the parts that is needed for the slingshot
 * system. It binds the modules and lets them interact with each other.
 * 
 * @author Julijan Katic
 *
 */
public final class SlingshotComponent {

	/** The scheduler or the driver of the simulator. */
	private final SimulationScheduling scheduling;

	/** The monitoring component of the simulator. */
	private final SimulationMonitoring monitoring;

	/** The simulation engine component of the simulator. */
	private final SimulationEngine simulationEngine;

	private final Simulation simulation;

	private final EventDispatcher dispatcher;

	/** The injector. */
	private final Injector injector;

	/**
	 * Initializes the model by loading all the modules and creating an injector of
	 * them. The required components must be part of the extension, otherwise an
	 * exception is thrown.
	 * <p>
	 * If the module to be loaded is of sub-type {@link SlingshotExtensionModule},
	 * then its {@code initialize()}-method will be called.
	 */
	public SlingshotComponent(final Builder builder) {
		this.injector = Guice.createInjector(builder.modules);
		this.scheduling = this.injector.getInstance(SimulationScheduling.class);
		this.monitoring = this.injector.getInstance(SimulationMonitoring.class);
		this.simulationEngine = this.injector.getInstance(SimulationEngine.class);
		this.simulation = this.injector.getInstance(Simulation.class);
		this.dispatcher = this.injector.getInstance(EventDispatcher.class);
	}

	/**
	 * @return the scheduling
	 */
	public SimulationScheduling getScheduling() {
		return this.scheduling;
	}

	/**
	 * @return the monitoring
	 */
	public SimulationMonitoring getMonitoring() {
		return this.monitoring;
	}

	/**
	 * @return the simulationEngine
	 */
	public SimulationEngine getSimulationEngine() {
		return this.simulationEngine;
	}

	public Simulation getSimulation() {
		return this.simulation;
	}

	public static Builder builder() {
		final ModuleLoader moduleLoader = new ModuleLoader();
		return new Builder(moduleLoader.getAllProviders());
	}

	public EventDispatcher getDispatcher() {
		return this.dispatcher;
	}

	public static final class Builder {
		private final List<Module> modules;

		private Builder(final List<? extends Module> initialModuleList) {
			this.modules = new ArrayList<>(initialModuleList);
		}

		public Builder withModule(final Module module) {
			this.modules.add(module);
			return this;
		}

		public SlingshotComponent build() {
			return new SlingshotComponent(this);
		}
	}

}
