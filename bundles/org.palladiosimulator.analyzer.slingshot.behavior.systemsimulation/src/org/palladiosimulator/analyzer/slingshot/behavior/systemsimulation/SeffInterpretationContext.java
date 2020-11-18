package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation;

import org.palladiosimulator.pcm.seff.AbstractAction;
import org.palladiosimulator.pcm.seff.ServiceEffectSpecification;

public class SeffInterpretationContext {

	private AbstractAction currentAction;
	private ServiceEffectSpecification seffModel;

	public AbstractAction getCurrentAction() {
		return currentAction;
	}

	public void setCurrentAction(final AbstractAction currentAction) {
		this.currentAction = currentAction;
	}

	public ServiceEffectSpecification getSeffModel() {
		return seffModel;
	}

	public void setSeffModel(final ServiceEffectSpecification seffModel) {
		this.seffModel = seffModel;
	}

}
