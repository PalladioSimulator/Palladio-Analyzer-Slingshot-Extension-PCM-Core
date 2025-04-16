package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.monitor.probes;

import javax.measure.Measure;
import javax.measure.quantity.Dimensionless;

import org.palladiosimulator.analyzer.slingshot.common.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.monitor.probes.EventBasedListProbe;
import org.palladiosimulator.metricspec.constants.MetricDescriptionConstants;
import org.palladiosimulator.scalablepcmgroups.ServiceGroup;

/**
 * Probe for the Number of Elements in a Service Group.
 *
 * The Number of Elements is always calculated with regard to a certain target group configuration,
 * i.e. only elements of a given target group are considered.
 *
 * @author Sarah Stieß
 *
 */
public final class NumberOfElementsInServiceGroupProbe extends EventBasedListProbe<Long, Dimensionless> {

    private final ServiceGroup serviceGroup;

    /**
     * Constructor for NumberOfElementsInServiceGroupProbe.
     *
     * TODO : fix metric description !!
     *
     * @param serviceGroup
     *            configuration of target group that will be measured.
     */
    public NumberOfElementsInServiceGroupProbe(final ServiceGroup serviceGroup) {
        super(MetricDescriptionConstants.NUMBER_OF_RESOURCE_CONTAINERS_OVER_TIME);
        // yes, this one subsumes
        this.serviceGroup = serviceGroup;
    }

    @Override
    public Measure<Long, Dimensionless> getMeasurement(final DESEvent event) {
        return Measure.valueOf(Long.valueOf(serviceGroup.getElements()
            .size()), Dimensionless.UNIT);
    }
}
