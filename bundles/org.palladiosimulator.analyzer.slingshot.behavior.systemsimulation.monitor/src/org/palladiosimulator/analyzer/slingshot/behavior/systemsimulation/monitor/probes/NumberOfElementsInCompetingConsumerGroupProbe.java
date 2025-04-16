package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.monitor.probes;

import javax.measure.Measure;
import javax.measure.quantity.Dimensionless;

import org.palladiosimulator.analyzer.slingshot.common.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.monitor.probes.EventBasedListProbe;
import org.palladiosimulator.metricspec.constants.MetricDescriptionConstants;
import org.palladiosimulator.scalablepcmgroups.CompetingConsumersGroup;

/**
 * Probe for the Number of Elements in a Competing Consumer Group.
 *
 * The Number of Elements is always calculated with regard to a certain target group configuration,
 * i.e. only elements of a given target group are considered.
 *
 * @author Sarah Stieß
 *
 */
public final class NumberOfElementsInCompetingConsumerGroupProbe extends EventBasedListProbe<Long, Dimensionless> {

    private final CompetingConsumersGroup competingConsumerGroupConfiguration;

    /**
     * Constructor for NumberOfElementsIncompetingConsumerGroupProbe.
     *
     * TODO : fix metric description !!
     *
     * @param competingConsumerGroup
     *            configuration of target group that will be measured.
     */
    public NumberOfElementsInCompetingConsumerGroupProbe(final CompetingConsumersGroup competingConsumerGroup) {
        super(MetricDescriptionConstants.NUMBER_OF_RESOURCE_CONTAINERS_OVER_TIME);
        // yes, this one subsumes
        this.competingConsumerGroupConfiguration = competingConsumerGroup;
    }

    @Override
    public Measure<Long, Dimensionless> getMeasurement(final DESEvent event) {
        return Measure.valueOf(Long.valueOf(competingConsumerGroupConfiguration.getElements()
            .size()), Dimensionless.UNIT);
    }
}
