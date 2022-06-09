package org.palladiosimulator.analyzer.slingshot.scalingpolicy.interpreter.adjustment;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.TriggerContext;
import org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.result.AdjustmentResult;
import org.palladiosimulator.analyzer.slingshot.scalingpolicy.interpreter.TargetGroupTable;
import org.palladiosimulator.analyzer.slingshot.simulation.core.entities.SimulationInformation;
import org.palladiosimulator.monitorrepository.MonitorRepository;
import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;
import org.palladiosimulator.pcm.resourceenvironment.ResourceEnvironment;

import de.unistuttgart.slingshot.spd.adjustments.RelativeAdjustment;

/**
 * The relative adjustment adds a number of [TODO]
 * 
 * @author Julijan Katic
 *
 */
public final class RelativeAdjustmentExecutor extends AbstractAdjustmentExecutor<RelativeAdjustment> {

	private static final Logger LOGGER = Logger.getLogger(RelativeAdjustmentExecutor.class);

	public RelativeAdjustmentExecutor(final RelativeAdjustment adjustmentType,
			final SimulationInformation simulationInformation,
			final Allocation allocation, final MonitorRepository monitorRepository) {
		super(adjustmentType, simulationInformation, allocation, monitorRepository);
	}

	@Override
	public AdjustmentResult onTrigger(final TriggerContext targetGroup) {
		this.adjustmentResultBuilder().withTriggerContext(targetGroup);
		final ResourceEnvironment environment = TargetGroupTable.instance()
				.getEnvironment(targetGroup.getTargetGroup());

		final int relativeNumber = (int) Math.floor(environment.getResourceContainer_ResourceEnvironment().size()
				* this.getAdjustmentType().getPercentageValue()/100);
		final int actualAdjustment = relativeNumber == 0 ? 
				environment.getResourceContainer_ResourceEnvironment().size() + this.getAdjustmentType().getMinAdjustmentValue()
				: environment.getResourceContainer_ResourceEnvironment().size() + relativeNumber;

		if (actualAdjustment < environment.getResourceContainer_ResourceEnvironment().size()) {
			// Decrease
			this.deleteContainers(environment,
					environment.getResourceContainer_ResourceEnvironment().size() - actualAdjustment);
		} else if (actualAdjustment > environment.getResourceContainer_ResourceEnvironment().size()) {
			// Increase
			final List<ResourceContainer> copiedContainers = new ArrayList<>(
					actualAdjustment - environment.getResourceContainer_ResourceEnvironment().size());
			this.copyContainers(environment, copiedContainers,
					actualAdjustment - environment.getResourceContainer_ResourceEnvironment().size());
			environment.getResourceContainer_ResourceEnvironment().addAll(copiedContainers);
		} else {
			return AdjustmentResult.NO_TRIGGER;
		}

		return this.adjustmentResult();
	}

}
