package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.monitor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;

import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.probes.NumberOfElementsInInfrastructureGroupProbe;
import org.palladiosimulator.analyzer.slingshot.common.annotations.Nullable;
import org.palladiosimulator.analyzer.slingshot.common.events.modelchanges.ModelAdjusted;
import org.palladiosimulator.analyzer.slingshot.core.extension.SimulationBehaviorExtension;
import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.Subscribe;
import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.eventcontract.EventCardinality;
import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.eventcontract.OnEvent;
import org.palladiosimulator.analyzer.slingshot.eventdriver.returntypes.Result;
import org.palladiosimulator.analyzer.slingshot.monitor.data.entities.ProbeTakenEntity;
import org.palladiosimulator.analyzer.slingshot.monitor.data.events.CalculatorRegistered;
import org.palladiosimulator.analyzer.slingshot.monitor.data.events.ProbeTaken;
import org.palladiosimulator.analyzer.slingshot.monitor.data.events.modelvisited.MeasurementSpecificationVisited;
import org.palladiosimulator.analyzer.slingshot.monitor.data.events.modelvisited.MonitorModelVisited;
import org.palladiosimulator.edp2.models.measuringpoint.MeasuringPoint;
import org.palladiosimulator.edp2.util.MetricDescriptionUtility;
import org.palladiosimulator.metricspec.constants.MetricDescriptionConstants;
import org.palladiosimulator.monitorrepository.MeasurementSpecification;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;
import org.palladiosimulator.probeframework.calculator.Calculator;
import org.palladiosimulator.probeframework.calculator.DefaultCalculatorProbeSets;
import org.palladiosimulator.probeframework.calculator.IGenericCalculatorFactory;
import org.palladiosimulator.scalablepcmgroupmeasuringpoint.InfrastructureGroupMeasuringPoint;
import org.palladiosimulator.scalablepcmgroups.InfrastructureGroup;
import org.palladiosimulator.scalablepcmgroups.ScalablePCMGroups;

/**
 *
 * Behavior to monitor the number of elements in a Elastic Infrastructure.
 *
 * The behavior creates Probes and Calculators for {@link SPDResourceContainerMeasuringPoint}s.
 *
 * For a {@link SPDResourceContainerMeasuringPoint}, the behavior creates a probe and a calculator
 * for the given resource container, iff the resource container is {@code unit} in any of the given
 * target configurations.
 *
 * The metric specification must be the base metric "number of resource containers".
 *
 * @author Sarah Stieß
 *
 */
@OnEvent(when = MonitorModelVisited.class, then = CalculatorRegistered.class, cardinality = EventCardinality.SINGLE)
@OnEvent(when = ModelAdjusted.class, then = ProbeTaken.class, cardinality = EventCardinality.SINGLE)
public class NumberOfElementsMonitorBehavior implements SimulationBehaviorExtension {

    private final IGenericCalculatorFactory calculatorFactory;
    private final ScalablePCMGroups semanticConfiguration;

    private final Map<ResourceContainer, NumberOfElementsInInfrastructureGroupProbe> probes = new HashMap<>();

    @Inject
    public NumberOfElementsMonitorBehavior(final IGenericCalculatorFactory calculatorFactory,
            final @Nullable ScalablePCMGroups semanticConfiguration) {
        this.calculatorFactory = calculatorFactory;
        this.semanticConfiguration = semanticConfiguration;
    }

    @Override
    public boolean isActive() {
        return this.semanticConfiguration != null && !this.semanticConfiguration.getTargetCfgs()
            .isEmpty();
    }

    @Subscribe
    public Result<CalculatorRegistered> onMeasurementSpecification(final MeasurementSpecificationVisited m) {
        final MeasurementSpecification spec = m.getEntity();
        final MeasuringPoint measuringPoint = spec.getMonitor()
            .getMeasuringPoint();

        if (measuringPoint instanceof InfrastructureGroupMeasuringPoint) {
            // Container MP --> register probe for EI where container is unit
            final InfrastructureGroupMeasuringPoint resourceContainerMeasuringPoint = (InfrastructureGroupMeasuringPoint) measuringPoint;

            if (MetricDescriptionUtility.metricDescriptionIdsEqual(spec.getMetricDescription(),
                    MetricDescriptionConstants.NUMBER_OF_RESOURCE_CONTAINERS)) {

                final Optional<InfrastructureGroup> serviceGroupCfg = this.semanticConfiguration.getTargetCfgs()
                    .stream()
                    .filter(group -> (group instanceof InfrastructureGroup))
                    .map(igroup -> ((InfrastructureGroup) igroup))
                    .filter(igroup -> igroup.getUnit()
                        .getId()
                        .equals(resourceContainerMeasuringPoint.getInfrastructureGroup()
                            .getUnit()
                            .getId()))
                    .findAny();

                if (serviceGroupCfg.isPresent()) {
                    final Calculator calculator = this.setupNumberOfElementsCalculator(resourceContainerMeasuringPoint,
                            this.calculatorFactory, serviceGroupCfg.get());
                    return Result.of(new CalculatorRegistered(calculator));
                }
            }
        }

        return Result.empty();
    }

    @Subscribe
    public Result<ProbeTaken> onModelAdjusted(final ModelAdjusted stateUpdated) {
        final Set<ProbeTaken> probesTaken = new HashSet<>();

        for (final NumberOfElementsInInfrastructureGroupProbe probe : probes.values()) {
            probe.takeMeasurement(stateUpdated);
            probesTaken.add(new ProbeTaken(ProbeTakenEntity.builder()
                .withProbe(probe)
                .build()));
        }
        return Result.of(probesTaken);
    }

    /**
     *
     * @param measuringPoint
     * @param calculatorFactory
     * @param eiCfg
     * @return
     */
    public Calculator setupNumberOfElementsCalculator(final MeasuringPoint measuringPoint,
            final IGenericCalculatorFactory calculatorFactory, final InfrastructureGroup eiCfg) {

        this.probes.putIfAbsent(eiCfg.getUnit(), new NumberOfElementsInInfrastructureGroupProbe(eiCfg));
        final NumberOfElementsInInfrastructureGroupProbe probe = this.probes.get(eiCfg.getUnit());
        return calculatorFactory.buildCalculator(MetricDescriptionConstants.NUMBER_OF_RESOURCE_CONTAINERS_OVER_TIME,
                measuringPoint, DefaultCalculatorProbeSets.createSingularProbeConfiguration(probe));
    }
}
