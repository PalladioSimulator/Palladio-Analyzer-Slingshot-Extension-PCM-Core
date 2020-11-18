package org.palladiosimulator.analyzer.slingshot.repositories.impl;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.palladiosimulator.analyzer.slingshot.repositories.SystemModelRepository;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.core.composition.Connector;
import org.palladiosimulator.pcm.core.composition.ProvidedDelegationConnector;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.repository.OperationProvidedRole;
import org.palladiosimulator.pcm.repository.OperationSignature;
import org.palladiosimulator.pcm.repository.ProvidedRole;
import org.palladiosimulator.pcm.repository.RepositoryComponent;
import org.palladiosimulator.pcm.repository.Signature;
import org.palladiosimulator.pcm.seff.ServiceEffectSpecification;
import org.palladiosimulator.pcm.system.System;
import org.palladiosimulator.pcm.usagemodel.EntryLevelSystemCall;

public class SystemModelRepositoryImpl implements SystemModelRepository {

	private final Logger LOGGER = Logger.getLogger(SystemModelRepositoryImpl.class);

	private System systemModel;

	@Override
	public void load(final System system) {
		this.systemModel = system;
	}

	@Override
	public AssemblyContext findAssemblyForEntryLevelSystemCall(final EntryLevelSystemCall systemCall) {

		final OperationProvidedRole operationProvidedRole = systemCall.getProvidedRole_EntryLevelSystemCall();
		final OperationSignature operationSignature = systemCall.getOperationSignature__EntryLevelSystemCall();

		final EList<Connector> connectors = systemModel.getConnectors__ComposedStructure();

//		for (final Connector connector : connectors) {
//			if (connector instanceof ProvidedDelegationConnector) {
//				ProvidedDelegationConnector delegationConnector = (ProvidedDelegationConnector) connector;
//				final OperationProvidedRole delegatedProvidedRole = delegationConnector.getInnerProvidedRole_ProvidedDelegationConnector();
//				
//				if (operationProvidedRole.equals(delegatedProvidedRole)) { // TODO: How to check for equality.
//					
//					final EList<AssemblyContext> contexts = systemModel.getAssemblyContexts__ComposedStructure();
//					
//					for (final AssemblyContext context : contexts) {
//						if (context.get)
//					}
//				}
//			}
//		}

		// TODO Auto-generated method stub
		return null;
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

	public ServiceEffectSpecification getDelegatedComponentSeff(final ProvidedDelegationConnector connector,
	        final Signature signature) {
		final ProvidedRole role = connector.getInnerProvidedRole_ProvidedDelegationConnector();
		final EList<AssemblyContext> assemblyContexts = systemModel.getAssemblyContexts__ComposedStructure();
		for (final AssemblyContext context : assemblyContexts) {
			final RepositoryComponent component = context.getEncapsulatedComponent__AssemblyContext();
			if (component instanceof BasicComponent) {
				final BasicComponent basicComponent = (BasicComponent) component;
				final EList<ServiceEffectSpecification> serviceEffectSpecifications = basicComponent
				        .getServiceEffectSpecifications__BasicComponent();

				for (final ServiceEffectSpecification spec : serviceEffectSpecifications) {
					if (spec.getDescribedService__SEFF().getId().equals(signature.getId())) {
						/*
						 * Found a component that specifies a SEFF of the provided role.
						 */
						return spec;
					}
				}
			}
		}
		return null;
	}
}
