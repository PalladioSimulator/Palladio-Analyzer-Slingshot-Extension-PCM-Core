package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.probes;

import javax.measure.Measure;
import javax.measure.quantity.Dimensionless;

import org.palladiosimulator.analyzer.slingshot.common.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.monitor.probes.EventBasedListProbe;
import org.palladiosimulator.metricspec.constants.MetricDescriptionConstants;
import org.palladiosimulator.scalablepcmgroups.InfrastructureGroup;

/**
 * Probe for the Number of Elements in a Elastic Infrastructure.
 *
 * The Number of Elements is always calculated with regard to a certain target group configuration,
 * i.e. only elements of a given target group are aconsidered.
 *
 * @author Sarah Stieß
 *
 */
public final class NumberOfElementsInInfrastructureGroupProbe extends EventBasedListProbe<Long, Dimensionless> {

    private final InfrastructureGroup infrastructureGroup;

    /**
     * Constructor for NumberOfElementsInResourceEnvironmentProbe.
     *
     * @param infrastructureGroup
     *            configuration of target group that will be measured.
     */
    public NumberOfElementsInInfrastructureGroupProbe(final InfrastructureGroup infrastructureGroup) {
        super(MetricDescriptionConstants.NUMBER_OF_RESOURCE_CONTAINERS_OVER_TIME);
        // yes, this one subsumes
        this.infrastructureGroup = infrastructureGroup;
    }

    @Override
    public Measure<Long, Dimensionless> getMeasurement(final DESEvent event) {
        return Measure.valueOf(Long.valueOf(infrastructureGroup.getElements()
            .size()), Dimensionless.UNIT);
    }
}
