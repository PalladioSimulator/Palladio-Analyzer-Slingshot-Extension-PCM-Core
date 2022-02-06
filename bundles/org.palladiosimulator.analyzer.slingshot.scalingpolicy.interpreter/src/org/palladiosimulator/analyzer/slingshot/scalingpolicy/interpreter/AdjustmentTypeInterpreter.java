package org.palladiosimulator.analyzer.slingshot.scalingpolicy.interpreter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.AdjustmentExecutor;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;
import org.palladiosimulator.pcm.resourceenvironment.ResourceEnvironment;

import spd.adjustmenttype.StepAdjustment;
import spd.adjustmenttype.util.AdjustmenttypeSwitch;

public class AdjustmentTypeInterpreter extends AdjustmenttypeSwitch<AdjustmentExecutor> {

	private static final Logger LOGGER = Logger.getLogger(AdjustmentTypeInterpreter.class);

	@Override
	public AdjustmentExecutor caseStepAdjustment(final StepAdjustment object) {
		return targetGroup -> {
			LOGGER.info("Target Group Step Adjustment Executed");
			final int targetCopy = object.getStepValue();
			final ResourceEnvironment resourceEnvironment = targetGroup.getResourceEnvironment();
			final List<ResourceContainer> newResourceContainers = new ArrayList<>(
					resourceEnvironment.getResourceContainer_ResourceEnvironment().size() * targetCopy);
			for (int i = 0; i < targetCopy; i++) {
				final Collection<ResourceContainer> copy = EcoreUtil
						.copyAll(resourceEnvironment.getResourceContainer_ResourceEnvironment());
				newResourceContainers.addAll(copy);
			}
			resourceEnvironment.getResourceContainer_ResourceEnvironment().addAll(newResourceContainers);
		};
	}

}
