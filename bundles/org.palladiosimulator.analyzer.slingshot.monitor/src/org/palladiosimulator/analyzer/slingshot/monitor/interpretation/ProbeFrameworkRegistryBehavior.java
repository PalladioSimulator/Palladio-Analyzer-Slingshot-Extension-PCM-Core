package org.palladiosimulator.analyzer.slingshot.monitor.interpretation;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.eclipse.emf.ecore.EClass;
import org.palladiosimulator.analyzer.slingshot.monitor.data.ProbeTaken;
import org.palladiosimulator.analyzer.slingshot.simulation.events.ModelPassedEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.SimulationBehaviorExtension;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.EventCardinality;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.OnEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.Reified;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.results.ResultEvent;
import org.palladiosimulator.monitorrepository.MeasurementSpecification;
import org.palladiosimulator.monitorrepository.Monitor;
import org.palladiosimulator.monitorrepository.MonitorRepository;
import org.palladiosimulator.monitorrepository.MonitorRepositoryPackage;
import org.palladiosimulator.pcm.core.entity.Entity;
import org.palladiosimulator.probeframework.calculator.IGenericCalculatorFactory;
import org.palladiosimulator.probeframework.probes.TriggeredProbe;

import com.google.common.eventbus.Subscribe;

import de.uka.ipd.sdq.simucomframework.model.SimuComModel;

@OnEvent(when = ModelPassedEvent.class, whenReified = Entity.class, then = ProbeTaken.class, cardinality = EventCardinality.SINGLE)
public class ProbeFrameworkRegistryBehavior implements SimulationBehaviorExtension {

	private final SimuComModel simuComModel;
	private final IGenericCalculatorFactory calculatorFactory;
	private final MonitorRepository monitorRepository;

	private final Map<String, List<TriggeredProbe>> currentTimeProbes = new HashMap<>();

	@Inject
	public ProbeFrameworkRegistryBehavior(
			final SimuComModel simuComModel,
			final IGenericCalculatorFactory calculatorFactory,
			final MonitorRepository monitorRepository) {
		this.simuComModel = simuComModel;
		this.calculatorFactory = calculatorFactory;
		this.monitorRepository = monitorRepository;
	}

	@Subscribe
	public ResultEvent<ProbeTaken> onModelElementPassed(
			@Reified(Entity.class) final ModelPassedEvent<Entity> passedEvent) {
		if (this.currentTimeProbes.containsKey(passedEvent.getModelElement().getId())) {
			this.currentTimeProbes.get(passedEvent.getModelElement().getId())
					.get(0)
					.takeMeasurement();
			return ResultEvent.of(new ProbeTaken(null));
		}
		return ResultEvent.empty();
	}

	private Collection<MeasurementSpecification> getMeasurementSpecificationsForProcessingType(
			final EClass processingTypeEClass) {
		assert processingTypeEClass != null;
		if (!MonitorRepositoryPackage.Literals.PROCESSING_TYPE.isSuperTypeOf(processingTypeEClass)) {
			throw new IllegalArgumentException("Given EClass object does not represent a "
					+ MonitorRepositoryPackage.Literals.PROCESSING_TYPE.getName() + "!");
		}
		return this.filterMeasurementSpecifications(spec -> processingTypeEClass.isInstance(spec.getProcessingType()));
	}

	private Collection<MeasurementSpecification> filterMeasurementSpecifications(
			final Predicate<? super MeasurementSpecification> predicate) {
		assert predicate != null;
		return this.monitorRepository.getMonitors().stream()
				.filter(Monitor::isActivated)
				.flatMap(monitor -> monitor.getMeasurementSpecifications().stream())
				.filter(predicate)
				.collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
	}
}
