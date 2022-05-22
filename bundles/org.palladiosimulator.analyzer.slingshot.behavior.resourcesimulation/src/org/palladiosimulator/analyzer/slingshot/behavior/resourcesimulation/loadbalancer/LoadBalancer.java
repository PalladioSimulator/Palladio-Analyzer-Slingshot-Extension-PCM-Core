package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.loadbalancer;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.resource.ResourceDemandRequest;
import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.allocation.AllocationContext;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.resourceenvironment.ResourceEnvironment;

public final class LoadBalancer {

	private static final Logger LOGGER = Logger.getLogger(LoadBalancer.class);

	private final Map<AllocationContext, Integer> jobs = new HashMap<>();
	private final Allocation allocation;

	public LoadBalancer(final ResourceEnvironment environment,
			final Allocation allocation) {

		this.allocation = Objects.requireNonNull(allocation);
		Objects.requireNonNull(environment);

		allocation.getAllocationContexts_Allocation().stream()
				.forEach(env -> this.jobs.computeIfAbsent(env, rEnv -> 0));
	}

	public AllocationContext getResourceContainer(final ResourceDemandRequest request) {
		final AssemblyContext assemblyContext = request.getAssemblyContext();
		final Entry<AllocationContext, Integer> minEntry = this.jobs.entrySet().stream()
				.filter(entry -> entry.getKey().getAssemblyContext_AllocationContext().getId()
						.equals(assemblyContext.getId()))
				.min((entry1, entry2) -> Integer.compare(entry1.getValue(), entry2.getValue()))
				.orElseThrow();
		minEntry.setValue(minEntry.getValue() + 1);
		return minEntry.getKey();
	}
}
