package org.palladiosimulator.analyzer.slingshot.scalingpolicy.interpreter;

import java.util.Objects;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.resource.Resource;
import org.palladiosimulator.analyzer.slingshot.common.utils.ResourceUtils;
import org.palladiosimulator.analyzer.slingshot.monitor.data.MeasurementMade;
import org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.AbstractTriggerEvent;
import org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.TriggerContext;
import org.palladiosimulator.analyzer.slingshot.simulation.core.entities.SimulationInformation;
import org.palladiosimulator.analyzer.slingshot.simulation.core.events.ConfigurationStarted;
import org.palladiosimulator.analyzer.slingshot.simulation.core.events.SimulationFinished;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.SimulationBehaviorExtension;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.EventCardinality;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.OnEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.results.ResultEvent;

import com.google.common.eventbus.Subscribe;

import spd.SPD;
import spd.ScalingPolicy;
import spd.targetgroup.TargetGroup;

@OnEvent(when = AbstractTriggerEvent.class, then = {})
@OnEvent(when = ConfigurationStarted.class, then = AbstractTriggerEvent.class, cardinality = EventCardinality.MANY)
@OnEvent(when = SimulationFinished.class, then = {})
@OnEvent(when = MeasurementMade.class, then = AbstractTriggerEvent.class, cardinality = EventCardinality.MANY)
public class ScalingBehavior implements SimulationBehaviorExtension {

	private static final Logger LOGGER = Logger.getLogger(ScalingBehavior.class);

	private final SPD spd;
	private final SimulationInformation simulationInformation;
	private final MonitorTriggerMapper monitorTriggerMapper = new MonitorTriggerMapper();

	@Inject
	public ScalingBehavior(final SPD spd, final SimulationInformation simulationInformation) {
		this.spd = Objects.requireNonNull(spd);
		this.simulationInformation = simulationInformation;
	}

	@Subscribe
	public ResultEvent<?> onSimulationStarted(final ConfigurationStarted configurationStarted) {
		final var interpreter = new ScalingPolicyDefinitionInterpreterLegacy(this.simulationInformation);
		return ResultEvent.of(interpreter.doSwitch(this.spd));
	}

	/**
	 * No matter what trigger had happened, all behavior are customly created there.
	 */
	@Subscribe
	public ResultEvent<?> onTrigger(final AbstractTriggerEvent trigger) {
		trigger.getContext().executeTrigger();
		return ResultEvent.empty();
	}

	public ResultEvent<?> onMeasurementMade(final MeasurementMade measurementMade) {
		final TriggerContext context = this.monitorTriggerMapper.get(measurementMade.getEntity().getMetricDesciption().getId());
		// TODO: Check if context is null
		context.executeTrigger();
		return ResultEvent.empty();
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
}
