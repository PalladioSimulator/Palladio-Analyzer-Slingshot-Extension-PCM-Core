package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.repository;

import java.util.Optional;

import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.core.composition.ProvidedDelegationConnector;
import org.palladiosimulator.pcm.repository.ProvidedRole;
import org.palladiosimulator.pcm.repository.RequiredRole;
import org.palladiosimulator.pcm.repository.Signature;
import org.palladiosimulator.pcm.seff.ServiceEffectSpecification;
import org.palladiosimulator.pcm.system.System;

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

	Optional<ServiceEffectSpecification> findSeffFromRequiredRole(RequiredRole requiredRole, Signature signature);

	Optional<AssemblyContext> findAssemblyContextFromRequiredRole(RequiredRole requiredRole);

	Optional<ProvidedDelegationConnector> getConnectedProvidedDelegationConnector(ProvidedRole providedRole);

	Optional<ServiceEffectSpecification> getDelegatedComponentSeff(ProvidedDelegationConnector connector,
			Signature signature);

	Optional<ServiceEffectSpecification> getSeffFromProvidedRole(ProvidedRole role, Signature signature);

	static SystemModelRepository getDefaultInstance() {
		return INSTANCE;
	}

	static final SystemModelRepository INSTANCE = new SystemModelRepositoryImpl();

	Optional<AssemblyContext> findAssemblyContextByProvidedRole(ProvidedRole role);
}
