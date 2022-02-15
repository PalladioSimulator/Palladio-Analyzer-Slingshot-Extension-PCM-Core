package org.palladiosimulator.analyzer.slingshot.scalingpolicy.interpreter;

import java.util.Objects;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.resource.Resource;
import org.palladiosimulator.analyzer.slingshot.common.utils.ResourceUtils;
import org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.AbstractTriggerEvent;
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
public class ScalingBehavior implements SimulationBehaviorExtension {

	private static final Logger LOGGER = Logger.getLogger(ScalingBehavior.class);

	private final SPD spd;

	@Inject
	public ScalingBehavior(final SPD spd) {
		this.spd = Objects.requireNonNull(spd);
	}

	@Subscribe
	public ResultEvent<?> onSimulationStarted(final ConfigurationStarted configurationStarted) {
		final var interpreter = new ScalingPolicyDefinitionInterpreter();
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
