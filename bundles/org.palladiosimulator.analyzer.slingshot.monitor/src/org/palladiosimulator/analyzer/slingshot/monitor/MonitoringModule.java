package org.palladiosimulator.analyzer.slingshot.monitor;

import org.palladiosimulator.analyzer.slingshot.simulation.api.SimulationMonitoring;
import org.palladiosimulator.probeframework.ProbeFrameworkContext;
import org.palladiosimulator.probeframework.calculator.IGenericCalculatorFactory;
import org.palladiosimulator.probeframework.calculator.IObservableCalculatorRegistry;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * The MonitoringModule implements the monitor (interpretation) behavior, such
 * as creating the corresponding recorders and aggregators.
 * <p>
 * This also adds the {@link ProbeFrameworkContext} and
 * {@link IGenericCalculatorFactory} directly to the module so that they can be
 * injected directly without the need of injecting a
 * {@link SimulationMonitoring}.
 * 
 * @author Julijan Katic
 */
public class MonitoringModule extends AbstractModule {

	@Override
	protected void configure() {
		super.configure();
		this.bind(SimulationMonitoring.class).to(GeneralMonitor.class);
	}

	@Provides
	public ProbeFrameworkContext getProbeFrameworkContext(final SimulationMonitoring monitoring) {
		return monitoring.getProbeFrameworkContext();
	}

	@Provides
	public IGenericCalculatorFactory getCalculatorFactory(final SimulationMonitoring monitoring) {
		return monitoring.getCalculatorFactory();
	}

	@Provides
	public IObservableCalculatorRegistry calculatorRegistry(final ProbeFrameworkContext probeFrameworkContext) {
		return probeFrameworkContext.getCalculatorRegistry();
	}

}
