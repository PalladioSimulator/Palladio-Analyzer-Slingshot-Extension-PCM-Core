package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.repository;

import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.core.composition.ProvidedDelegationConnector;
import org.palladiosimulator.pcm.core.entity.InterfaceProvidingEntity;
import org.palladiosimulator.pcm.repository.ProvidedRole;
import org.palladiosimulator.pcm.repository.RequiredRole;
import org.palladiosimulator.pcm.repository.Signature;
import org.palladiosimulator.pcm.seff.ServiceEffectSpecification;
import org.palladiosimulator.pcm.system.System;
import org.palladiosimulator.pcm.usagemodel.EntryLevelSystemCall;

import com.google.inject.ImplementedBy;

/**
 * A repository for system models that handles further handlers for the
 * {@code System} model.
 * 
 * @author Julijan Katic
 */
@ImplementedBy(SystemModelRepositoryImpl.class)
public interface SystemModelRepository {

	void load(System system);

	AssemblyContext findAssemblyForEntryLevelSystemCall(EntryLevelSystemCall systemCall);

	ProvidedDelegationConnector getConnectedProvidedDelegationConnector(ProvidedRole providedRole);

	ServiceEffectSpecification getDelegatedComponentSeff(final ProvidedDelegationConnector connector,
	        final Signature signature);

	ServiceEffectSpecification findSeffFromRequiredRole(final RequiredRole requiredRole,
	        final Signature signature);

	ServiceEffectSpecification getSeffFromProvidedRole(ProvidedRole role, Signature signature);

	AssemblyContext findAssemblyContextFromRequiredRole(RequiredRole requiredRole);

	InterfaceProvidingEntity findProvidingEntity(ProvidedRole providedRole);
}