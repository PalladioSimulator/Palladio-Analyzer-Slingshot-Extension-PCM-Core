package org.palladiosimulator.analyzer.slingshot.repositories;

import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.system.System;
import org.palladiosimulator.pcm.usagemodel.EntryLevelSystemCall;

public interface SystemModelRepository {

	void load(System system);
	
	AssemblyContext findAssemblyForEntryLevelSystemCall(EntryLevelSystemCall systemCall);
	
}
