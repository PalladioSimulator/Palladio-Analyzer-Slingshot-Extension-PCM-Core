package org.palladiosimulator.analyzer.slingshot.monitor;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.palladiosimulator.analyzer.slingshot.monitor.probe.EventProbe;
import org.palladiosimulator.analyzer.slingshot.monitor.probe.ProbeProviderPair;
import org.palladiosimulator.analyzer.slingshot.monitor.probe.ProbeTrigger;
import org.palladiosimulator.analyzer.slingshot.simulation.core.SimulationMonitoring;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.probeframework.probes.Probe;
import org.palladiosimulator.probeframework.probes.TriggeredProbe;

public class Monitoring implements SimulationMonitoring {

	private final List<ProbeProviderPair> probeProviders;

	public Monitoring() {
		/* TODO: Register probe providers appropriately by using extension points */
		this.probeProviders = new ArrayList<>();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * Furthermore, the event should be probable either by being annotated by
	 * {@link ProbeTrigger} or by registering probe providers.
	 */
	@Override
	public void publishProbeEvent(final DESEvent event) {
		this.callProbeTriggers(event);
		this.callProbeProviders(event);
	}

	/**
	 * @param event
	 */
	private void callProbeTriggers(final DESEvent event) {
		final ProbeTrigger[] probeTriggers = event.getClass().getAnnotationsByType(ProbeTrigger.class);
		for (final ProbeTrigger probeTrigger : probeTriggers) {
			final Class<? extends EventProbe<?, ?, ?>> eventProbeClazz = probeTrigger.value();

			try {
				final Constructor<? extends EventProbe<?, ?, ?>> constructor = eventProbeClazz
						.getConstructor(DESEvent.class);

				final EventProbe<?, ?, ?> eventProbe = constructor.newInstance(event);
				eventProbe.takeMeasurement();

			} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	/**
	 * @param event
	 */
	private void callProbeProviders(final DESEvent event) {
		for (final ProbeProviderPair probeProviderPair : this.probeProviders) {
			final Class<?> parameterType = probeProviderPair.getProviderMethod().getParameterTypes()[0];
			if (event.getClass().isAssignableFrom(parameterType)) {
				try {
					final Object returnedObj = probeProviderPair.getProviderMethod()
							.invoke(probeProviderPair.getInstance(), event);
					if (!(returnedObj instanceof Probe)) {
						throw new IllegalArgumentException(
								"The probe provider should return an instance of type Probe.");
					}

					/* TODO: Check if probe is a TriggerProbe: Look into EventProbes */
					final TriggeredProbe probe = (TriggeredProbe) returnedObj;
					probe.takeMeasurement();
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}
