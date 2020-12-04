package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.repository;

import java.util.Optional;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.palladiosimulator.pcm.core.composition.AssemblyConnector;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.core.composition.Connector;
import org.palladiosimulator.pcm.core.composition.ProvidedDelegationConnector;
import org.palladiosimulator.pcm.core.entity.InterfaceProvidingEntity;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.repository.OperationProvidedRole;
import org.palladiosimulator.pcm.repository.OperationSignature;
import org.palladiosimulator.pcm.repository.ProvidedRole;
import org.palladiosimulator.pcm.repository.RepositoryComponent;
import org.palladiosimulator.pcm.repository.RequiredRole;
import org.palladiosimulator.pcm.repository.Signature;
import org.palladiosimulator.pcm.seff.ServiceEffectSpecification;
import org.palladiosimulator.pcm.system.System;
import org.palladiosimulator.pcm.usagemodel.EntryLevelSystemCall;

public class SystemModelRepositoryImpl implements SystemModelRepository {

	private static final Logger LOGGER = Logger.getLogger(SystemModelRepositoryImpl.class);

	private System systemModel;

	@Override
	public void load(final System system) {
		this.systemModel = system;
	}

	@Override
	public AssemblyContext findAssemblyForEntryLevelSystemCall(final EntryLevelSystemCall systemCall) {

		final OperationProvidedRole operationProvidedRole = systemCall.getProvidedRole_EntryLevelSystemCall();
		final OperationSignature operationSignature = systemCall.getOperationSignature__EntryLevelSystemCall();

		final ProvidedDelegationConnector connection = getConnectedProvidedDelegationConnector(operationProvidedRole);

		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServiceEffectSpecification findSeffFromRequiredRole(final RequiredRole requiredRole,
	        final Signature signature) {
		final Optional<AssemblyConnector> optionalConnector = systemModel.getConnectors__ComposedStructure().stream()
		        .filter(connector -> connector instanceof AssemblyConnector)
		        .map(connector -> (AssemblyConnector) connector)
		        .filter(assemblyConnector -> assemblyConnector.getRequiredRole_AssemblyConnector().getId()
		                .equals(requiredRole.getId()))
		        .findFirst();

		if (optionalConnector.isPresent()) {
			final AssemblyConnector assemblyConnector = optionalConnector.get();
			final AssemblyContext providingContext = assemblyConnector.getProvidingAssemblyContext_AssemblyConnector();
			final BasicComponent basicComponent = (BasicComponent) providingContext
			        .getEncapsulatedComponent__AssemblyContext();
			return getSeffFromBasicComponent(basicComponent, signature);
		} else {
			return null;
		}
	}

	public AssemblyContext findAssemblyContextFromRepositoryComponent(final RepositoryComponent component) {
		return this.systemModel.getAssemblyContexts__ComposedStructure().stream()
		        .filter(context -> context.getEncapsulatedComponent__AssemblyContext().getId()
		                .equals(component.getId()))
		        .findFirst()
		        .get();
	}

	@Override
	public AssemblyContext findAssemblyContextFromRequiredRole(final RequiredRole requiredRole) {
		return this.systemModel.getConnectors__ComposedStructure().stream()
		        .filter(connector -> connector instanceof AssemblyConnector)
		        .map(connector -> (AssemblyConnector) connector)
		        .filter(assemblyConnector -> assemblyConnector.getRequiredRole_AssemblyConnector().getId()
		                .equals(requiredRole.getId()))
		        .map(assemblyConnector -> assemblyConnector.getRequiringAssemblyContext_AssemblyConnector())
		        .findFirst().get();
	}

	@Override
	public ProvidedDelegationConnector getConnectedProvidedDelegationConnector(final ProvidedRole providedRole) {
		final EList<ProvidedRole> providedRoles = systemModel.getProvidedRoles_InterfaceProvidingEntity();

		for (final ProvidedRole systemProvidedRole : providedRoles) {
			if (systemProvidedRole.getId().equals(providedRole.getId())) {

				for (final Connector connector : systemModel.getConnectors__ComposedStructure()) {
					if (connector instanceof ProvidedDelegationConnector) {
						final ProvidedDelegationConnector delegationConnector = (ProvidedDelegationConnector) connector;
						if (delegationConnector.getOuterProvidedRole_ProvidedDelegationConnector().getId()
						        .equals(providedRole.getId())) {
							return delegationConnector;
						}
					}
				}

			}
		}

		return null;
	}

	@Override
	public ServiceEffectSpecification getDelegatedComponentSeff(final ProvidedDelegationConnector connector,
	        final Signature signature) {
		final ProvidedRole role = connector.getInnerProvidedRole_ProvidedDelegationConnector();
		return getSeffFromProvidedRole(role, signature);
	}

	@Override
	public ServiceEffectSpecification getSeffFromProvidedRole(final ProvidedRole role, final Signature signature) {
		final EList<AssemblyContext> assemblyContexts = systemModel.getAssemblyContexts__ComposedStructure();
		for (final AssemblyContext context : assemblyContexts) {
			final RepositoryComponent component = context.getEncapsulatedComponent__AssemblyContext();
			final boolean correctProvidedRole = component.getProvidedRoles_InterfaceProvidingEntity().stream()
			        .anyMatch(providedRole -> providedRole.getId().equals(role.getId()));

			if (correctProvidedRole && component instanceof BasicComponent) {
				final BasicComponent basicComponent = (BasicComponent) component;
				final ServiceEffectSpecification spec = getSeffFromBasicComponent(basicComponent, signature);
				if (spec != null) {
					return spec;
				}
			}
		}
		return null;
	}

	public ServiceEffectSpecification getSeffFromBasicComponent(final BasicComponent basicComponent,
	        final Signature signature) {
		final EList<ServiceEffectSpecification> serviceEffectSpecifications = basicComponent
		        .getServiceEffectSpecifications__BasicComponent();

		for (final ServiceEffectSpecification spec : serviceEffectSpecifications) {
			if (spec.getDescribedService__SEFF().getId().equals(signature.getId())) {
				return spec;
			}
		}
		return null;
	}

	@Override
	public InterfaceProvidingEntity findProvidingEntity(final ProvidedRole providedRole) {
		if (systemModel.getProvidedRoles_InterfaceProvidingEntity().stream().anyMatch(
		        systemProvidedRole -> systemProvidedRole.getId().equals(providedRole.getId()))) {
			return systemModel;
		}
		return systemModel.getAssemblyContexts__ComposedStructure().stream()
		        .map(context -> context.getEncapsulatedComponent__AssemblyContext())
		        .filter(component -> component.getProvidedRoles_InterfaceProvidingEntity().stream()
		                .anyMatch(role -> role.getId().equals(providedRole.getId())))
		        .findFirst()
		        .get(); // TODO
	}
}
