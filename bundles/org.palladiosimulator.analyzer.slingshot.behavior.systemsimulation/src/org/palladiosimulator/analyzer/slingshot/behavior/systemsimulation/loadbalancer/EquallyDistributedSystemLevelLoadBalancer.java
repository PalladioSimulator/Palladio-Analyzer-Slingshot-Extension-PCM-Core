package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.loadbalancer;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.repository.OperationProvidedRole;
import org.palladiosimulator.pcm.repository.ProvidedRole;
import org.palladiosimulator.pcm.repository.RepositoryComponent;
import org.palladiosimulator.pcm.system.System;

/**
 * Implements the {@link SystemLevelLoadBalancer} with a uniform distribution.
 * 
 * @author Julijan Katic
 */
public final class EquallyDistributedSystemLevelLoadBalancer implements SystemLevelLoadBalancer {

	private final System systemModel;

	@Inject
	public EquallyDistributedSystemLevelLoadBalancer(final Allocation allocation) {
		this.systemModel = allocation.getSystem_Allocation();
	}

	@Override
	public Optional<AssemblyContext> getAssemblyContext(final OperationProvidedRole providedRole) {
		final double randomNumber = Math.random(); // Approximiatly uniform distribution

		final List<AssemblyContext> assemblyContext = this.systemModel.getAssemblyContexts__ComposedStructure().stream()
				.filter(context -> this.isProvidedRoleInComponent(context.getEncapsulatedComponent__AssemblyContext(),
						providedRole))
				.collect(Collectors.toList());

		if (assemblyContext.isEmpty()) {
			return Optional.empty();
		} else {
			return Optional.of(assemblyContext.get((int) Math.floor(assemblyContext.size() * randomNumber)));
		}
	}

	private boolean isProvidedRoleInComponent(final RepositoryComponent component, final ProvidedRole role) {
		return component.getProvidedRoles_InterfaceProvidingEntity().stream()
				.anyMatch(providedRole -> providedRole.getId().equals(role.getId()));
	}
}
