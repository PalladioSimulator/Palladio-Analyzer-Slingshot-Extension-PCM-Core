package org.palladiosimulator.analyzer.slingshot.monitor.interpretation;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import org.palladiosimulator.analyzer.slingshot.monitor.data.MonitorModelVisited;
import org.palladiosimulator.analyzer.slingshot.monitor.data.MonitoringEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.api.SimulationScheduling;
import org.palladiosimulator.monitorrepository.MeasurementSpecification;
import org.palladiosimulator.monitorrepository.Monitor;
import org.palladiosimulator.monitorrepository.MonitorRepository;
import org.palladiosimulator.monitorrepository.ProcessingType;
import org.palladiosimulator.monitorrepository.util.MonitorRepositorySwitch;
import org.palladiosimulator.probeframework.calculator.IObservableCalculatorRegistry;

import de.uka.ipd.sdq.identifier.Identifier;

/**
 * Interpretes a Monitor Repository model using a switch.
 * 
 * @author Julijan Katic
 */
public final class MonitorRepositoryInterpreter extends MonitorRepositorySwitch<Set<MonitoringEvent>> {

	private static final Logger LOGGER = Logger.getLogger(MonitorRepositoryInterpreter.class);

	private final IObservableCalculatorRegistry calculatorRegistry;
	private final SimulationScheduling scheduling;

	public MonitorRepositoryInterpreter(
			final IObservableCalculatorRegistry calculatorRegistry,
			final SimulationScheduling scheduling) {
		this.calculatorRegistry = calculatorRegistry;
		this.scheduling = scheduling;
	}

	@Override
	public Set<MonitoringEvent> caseMonitor(final Monitor object) {
		return object.getMeasurementSpecifications().stream()
				.flatMap(spec -> this.doSwitch(spec).stream())
				.collect(Collectors.toSet());
	}

	@Override
	public Set<MonitoringEvent> caseMeasurementSpecification(final MeasurementSpecification object) {
		final Set<MonitoringEvent> result = new HashSet<>(this.doSwitch(object.getProcessingType()));
		result.add(new MonitorModelVisited<>(object));
		return Collections.unmodifiableSet(result);
	}

	@Override
	public Set<MonitoringEvent> caseProcessingType(final ProcessingType object) {
		return Set.of(new MonitorModelVisited<>(object));
	}

	@Override
	public Set<MonitoringEvent> caseMonitorRepository(final MonitorRepository object) {
		return object.getMonitors().stream()
				.flatMap(monitor -> this.doSwitch(monitor).stream())
				.collect(Collectors.toSet());
	}

	@Override
	protected Set<MonitoringEvent> doSwitch(final int classifierID, final EObject theEObject) {
		final Optional<Set<MonitoringEvent>> result = Optional.ofNullable(super.doSwitch(classifierID, theEObject));
		return result.orElse(Collections.emptySet());
	}

	private static void logCannotBeInterpreted(final String type, final Identifier object, final String className) {
		LOGGER.info(type + " cannot be interpreted in the standard monitoring plugin: <" + object.getId() + ">("
				+ className + ")");
	}
}
