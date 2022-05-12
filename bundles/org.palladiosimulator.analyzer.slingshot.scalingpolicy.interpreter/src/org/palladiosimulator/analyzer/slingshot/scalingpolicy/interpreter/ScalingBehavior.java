package org.palladiosimulator.analyzer.slingshot.scalingpolicy.interpreter;

import java.util.Objects;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.resource.Resource;
import org.palladiosimulator.analyzer.slingshot.common.utils.ResourceUtils;
import org.palladiosimulator.analyzer.slingshot.monitor.data.MeasurementMade;
import org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.AbstractTriggerEvent;
import org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.TriggerContext;
import org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.events.AdjustmentNotMade;
import org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.events.ModelAdjusted;
import org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.result.AdjustmentResult;
import org.palladiosimulator.analyzer.slingshot.simulation.core.entities.SimulationInformation;
import org.palladiosimulator.analyzer.slingshot.simulation.core.events.ConfigurationStarted;
import org.palladiosimulator.analyzer.slingshot.simulation.core.events.SimulationFinished;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.SimulationBehaviorExtension;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.EventCardinality;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.OnEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.results.ResultEvent;
import org.palladiosimulator.monitorrepository.MonitorRepository;
import org.palladiosimulator.pcm.allocation.Allocation;

import com.google.common.eventbus.Subscribe;

import spd.SPD;
import spd.ScalingPolicy;
import spd.targetgroup.TargetGroup;

@OnEvent(when = AbstractTriggerEvent.class, then = { ModelAdjusted.class,
		AdjustmentNotMade.class }, cardinality = EventCardinality.SINGLE)
@OnEvent(when = ConfigurationStarted.class, then = {}, cardinality = EventCardinality.MANY)
@OnEvent(when = SimulationFinished.class, then = {})
@OnEvent(when = MeasurementMade.class, then = { ModelAdjusted.class,
		AdjustmentNotMade.class }, cardinality = EventCardinality.SINGLE)
public class ScalingBehavior implements SimulationBehaviorExtension {

	private static final Logger LOGGER = Logger.getLogger(ScalingBehavior.class);

	private final SPD spd;
	private final SimulationInformation simulationInformation;
	private final MonitorTriggerMapper monitorTriggerMapper = new MonitorTriggerMapper();

	private final Allocation allocation;
	private final MonitorRepository monitorRepository;

	@Inject
	public ScalingBehavior(final SPD spd,
			final SimulationInformation simulationInformation,
			final Allocation allocation,
			final MonitorRepository monitorRepository) {
		this.spd = Objects.requireNonNull(spd);
		this.simulationInformation = simulationInformation;
		this.allocation = allocation;
		this.monitorRepository = monitorRepository;
	}

	@Subscribe
	public ResultEvent<?> onSimulationStarted(final ConfigurationStarted configurationStarted) {
		final var interpreter = new ScalingPolicyDefinitionInterpreter(this.simulationInformation, this.allocation,
				this.monitorRepository);
		interpreter.doSwitch(this.spd);
		return ResultEvent.empty();
	}

	/**
	 * No matter what trigger had happened, all behavior are customly created there.
	 * 
	 * TODO: this is only needed for Point in Time triggers. Maybe make this
	 * concrete?
	 */
	@Subscribe
	public ResultEvent<?> onTrigger(final AbstractTriggerEvent trigger) {
		return this.adjustmentResult(trigger.getContext());
	}

	@Subscribe
	public ResultEvent<?> onMeasurementMade(final MeasurementMade measurementMade) {
		final TriggerContext context = this.monitorTriggerMapper
				.get(measurementMade.getEntity().getMetricDesciption().getId());
		return this.adjustmentResult(context);
	}

	/**
	 * After the simulation has finished, we save the changes of all target groups
	 * into their respective model.
	 */
	@Subscribe
	public ResultEvent<?> onSimulationFinished(final SimulationFinished finished) {
		this.spd.getScalingpolicy().stream()
				.map(ScalingPolicy::getTargetgroup)
				.map(TargetGroup.class::cast)
				.map(TargetGroupTable.instance()::getEnvironment)
				.forEach(resourceEnvironment -> {
					LOGGER.info("Saving resource environment: " + resourceEnvironment.getEntityName());
					final Resource resource = resourceEnvironment.eResource();
					resource.getContents().add(resourceEnvironment);
					ResourceUtils.saveResource(resource);
				});
		return ResultEvent.empty();
	}

	private ResultEvent<?> adjustmentResult(final TriggerContext context) {
		final AdjustmentResult result = context.executeTrigger();

		if (result.isSuccess()) {
			return ResultEvent.of(new ModelAdjusted(result));
		} else {
			return ResultEvent.of(new AdjustmentNotMade(result));
		}
	}
}
