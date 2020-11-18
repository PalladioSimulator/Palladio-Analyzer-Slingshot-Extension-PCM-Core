package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.interpreters;

import java.util.Set;

import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.repository.ProvidedRole;
import org.palladiosimulator.pcm.repository.util.RepositorySwitch;
import org.palladiosimulator.pcm.system.System;

public class RepositoryInterpreter extends RepositorySwitch<Set<DESEvent>> {

	private System systemModel;

	@Override
	public Set<DESEvent> caseBasicComponent(final BasicComponent object) {

		return super.caseBasicComponent(object);
	}

	@Override
	public Set<DESEvent> caseProvidedRole(final ProvidedRole object) {
		// TODO Auto-generated method stub
		return super.caseProvidedRole(object);
	}

}
