package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.repository;

import java.util.Optional;

import org.apache.log4j.Logger;
import org.palladiosimulator.pcm.core.composition.AssemblyConnector;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.core.composition.ProvidedDelegationConnector;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.repository.ProvidedRole;
import org.palladiosimulator.pcm.repository.RepositoryComponent;
import org.palladiosimulator.pcm.repository.RequiredRole;
import org.palladiosimulator.pcm.repository.Signature;
import org.palladiosimulator.pcm.seff.ServiceEffectSpecification;
import org.palladiosimulator.pcm.system.System;

public class SystemModelRepositoryImpl implements SystemModelRepository {

	private static final Logger LOGGER = Logger.getLogger(SystemModelRepositoryImpl.class);

	private System systemModel;

	@Override
	public void load(final System system) {
		this.systemModel = system;
	}

	@Override
	public Optional<ServiceEffectSpecification> findSeffFromRequiredRole(final RequiredRole requiredRole,
			final Signature signature) {
		 return this.systemModel.getConnectors__ComposedStructure().stream()
				.filter(connector -> connector instanceof AssemblyConnector)
				.map(connector -> (AssemblyConnector) connector)
				.filter(assemblyConnector -> assemblyConnector
						.getRequiredRole_AssemblyConnector().getId().equals(requiredRole.getId()))
				.map(AssemblyConnector::getProvidingAssemblyContext_AssemblyConnector)
				.map(AssemblyContext::getEncapsulatedComponent__AssemblyContext)
				.filter(BasicComponent.class::isInstance)
				.map(BasicComponent.class::cast)
				.map(component -> this.getSeffFromBasicComponent(component, signature))
				.filter(Optional::isPresent)
				.map(Optional::get)
				.findFirst();
		
		
	}

	public Optional<AssemblyContext> findAssemblyContextFromRepositoryComponent(final RepositoryComponent component) {
		return this.systemModel.getAssemblyContexts__ComposedStructure().stream().filter(
				context -> context.getEncapsulatedComponent__AssemblyContext().getId().equals(component.getId()))
				.findFirst();
	}

	@Override
	public Optional<AssemblyContext> findAssemblyContextFromRequiredRole(final RequiredRole requiredRole) {
		return this.systemModel.getConnectors__ComposedStructure().stream()
				.filter(connector -> connector instanceof AssemblyConnector)
				.map(connector -> (AssemblyConnector) connector)
				.filter(assemblyConnector -> assemblyConnector.getRequiredRole_AssemblyConnector().getId()
						.equals(requiredRole.getId()))
				.map(AssemblyConnector::getRequiringAssemblyContext_AssemblyConnector).findFirst();
	}

	@Override
	public Optional<ProvidedDelegationConnector> getConnectedProvidedDelegationConnector(
			final ProvidedRole providedRole) {
		
		final boolean providedRolePresent = this.systemModel.getProvidedRoles_InterfaceProvidingEntity().stream()
			.filter(systemProvidedRole -> systemProvidedRole.getId().equals(providedRole.getId()))
			.findFirst()
			.isPresent();
		
		if (!providedRolePresent) {
			return Optional.empty();
		}
		
		return this.systemModel.getConnectors__ComposedStructure().stream()
			.filter(ProvidedDelegationConnector.class::isInstance)
			.map(ProvidedDelegationConnector.class::cast)
			.filter(connector -> connector.getId().equals(providedRole.getId()))
			.findFirst();
	}

	@Override
	public Optional<ServiceEffectSpecification> getDelegatedComponentSeff(final ProvidedDelegationConnector connector,
			final Signature signature) {
		final ProvidedRole role = connector.getInnerProvidedRole_ProvidedDelegationConnector();
		return this.getSeffFromProvidedRole(role, signature);
	}

	@Override
	public Optional<ServiceEffectSpecification> getSeffFromProvidedRole(final ProvidedRole role,
			final Signature signature) {
		
		return this.systemModel.getAssemblyContexts__ComposedStructure().stream()
			.map(AssemblyContext::getEncapsulatedComponent__AssemblyContext)
			.filter(component -> component.getProvidedRoles_InterfaceProvidingEntity().stream().anyMatch(
						providedRole -> providedRole.getId().equals(role.getId())
					))
			.filter(BasicComponent.class::isInstance)
			.map(BasicComponent.class::cast)
			.map(basicComponent -> this.getSeffFromBasicComponent(basicComponent, signature))
			.filter(Optional::isPresent)
			.map(Optional::get)
			.findFirst();
		
	}

	@Override
	public Optional<AssemblyContext> findAssemblyContextByProvidedRole(final ProvidedRole role) {
		return this.systemModel.getConnectors__ComposedStructure().stream()
				.filter(AssemblyConnector.class::isInstance)
				.map(AssemblyConnector.class::cast)
				.filter(connector -> connector.getProvidedRole_AssemblyConnector().getId().equals(role.getId()))
				.map(AssemblyConnector::getProvidingAssemblyContext_AssemblyConnector)
				.findFirst();
	}

	public Optional<ServiceEffectSpecification> getSeffFromBasicComponent(final BasicComponent basicComponent,
			final Signature signature) {
		
		return basicComponent.getServiceEffectSpecifications__BasicComponent().stream()
			.filter(spec -> spec.getDescribedService__SEFF().getId().equals(signature.getId()))
			.findFirst();
		
	}

}
