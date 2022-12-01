package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.probes;

import javax.measure.Measure;
import javax.measure.quantity.Dimensionless;

import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.resources.IPassiveResource;
//import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.resources.IPassiveResource;
import org.palladiosimulator.analyzer.slingshot.monitor.probes.EventBasedProbe;
import org.palladiosimulator.analyzer.slingshot.monitor.probes.EventDistinguisher;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.metricspec.constants.MetricDescriptionConstants;
import org.palladiosimulator.probeframework.measurement.ProbeMeasurement;

public class TakePassiveResourceStateProbe extends EventBasedProbe<Long, Dimensionless> {

	private final IPassiveResource passiveResource;

	public TakePassiveResourceStateProbe(
			final IPassiveResource passiveResource,
			final EventDistinguisher<? super DESEvent> distinguisher) {
		super(MetricDescriptionConstants.STATE_OF_PASSIVE_RESOURCE_METRIC, distinguisher);
		this.passiveResource = passiveResource;
	}

	public TakePassiveResourceStateProbe(
			final IPassiveResource passiveResource) {
		super(MetricDescriptionConstants.STATE_OF_PASSIVE_RESOURCE_METRIC);
		this.passiveResource = passiveResource;
	}

	@Override
	public Measure<Long, Dimensionless> getMeasurement(final DESEvent event) {
		return Measure.valueOf(this.passiveResource.getCurrentlyAvailable(), Dimensionless.UNIT);
	}

	@Override
	protected ProbeMeasurement getProbeMeasurement(final DESEvent event) {
		// TODO Auto-generated method stub
		return null;
	}

}
