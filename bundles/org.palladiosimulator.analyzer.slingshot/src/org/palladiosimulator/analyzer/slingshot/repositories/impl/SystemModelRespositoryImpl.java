package org.palladiosimulator.analyzer.slingshot.repositories.impl;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.repositories.SystemModelRepository;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.repository.OperationProvidedRole;
import org.palladiosimulator.pcm.system.System;
import org.palladiosimulator.pcm.usagemodel.EntryLevelSystemCall;
import org.palladiosimulator.pcm.usagemodel.UsageModel;

public class SystemModelRespositoryImpl implements SystemModelRepository {

	private final Logger LOGGER = Logger.getLogger(SystemModelRespositoryImpl.class);
	
	private System systemModel;

	
	@Override
	public void load(System system) {
		this.systemModel = system;
	}

	@Override
	public AssemblyContext findAssemblyForEntryLevelSystemCall(EntryLevelSystemCall systemCall) {
		
		OperationProvidedRole operationProvidedRole = systemCall.getProvidedRole_EntryLevelSystemCall();
		
		
		
		
		// TODO Auto-generated method stub
		return null;
	}

}
