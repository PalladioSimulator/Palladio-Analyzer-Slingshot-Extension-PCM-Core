package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.interpreters;

import org.palladiosimulator.pcm.core.composition.AssemblyConnector;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.core.composition.AssemblyInfrastructureConnector;
import org.palladiosimulator.pcm.core.composition.Connector;
import org.palladiosimulator.pcm.core.composition.RequiredDelegationConnector;
import org.palladiosimulator.pcm.core.composition.RequiredInfrastructureDelegationConnector;
import org.palladiosimulator.pcm.core.composition.util.CompositionSwitch;
import org.palladiosimulator.pcm.repository.RequiredRole;

public class ConnectorSelector extends CompositionSwitch<Connector> {

	private final AssemblyContext context;
	private final RequiredRole requiredRole;

	public ConnectorSelector(final AssemblyContext context, final RequiredRole requiredRole) {
		this.context = context;
		this.requiredRole = requiredRole;
	}

	@Override
	public Connector caseRequiredDelegationConnector(final RequiredDelegationConnector delegationConnector) {
		if (delegationConnector.getAssemblyContext_RequiredDelegationConnector() == context
		        && delegationConnector.getInnerRequiredRole_RequiredDelegationConnector() == requiredRole) {
			return delegationConnector;
		}
		return null;
	}

	@Override
	public Connector caseAssemblyConnector(final AssemblyConnector assemblyConnector) {
		if (assemblyConnector.getRequiringAssemblyContext_AssemblyConnector() == context
		        && assemblyConnector.getRequiredRole_AssemblyConnector() == requiredRole) {
			return assemblyConnector;
		}
		return null;
	}

	@Override
	public Connector caseAssemblyInfrastructureConnector(final AssemblyInfrastructureConnector connector) {
		if (connector.getRequiringAssemblyContext__AssemblyInfrastructureConnector() == context
		        && connector.getRequiredRole__AssemblyInfrastructureConnector() == requiredRole) {
			return connector;
		}
		return null;
	}

	@Override
	public Connector caseRequiredInfrastructureDelegationConnector(
	        final RequiredInfrastructureDelegationConnector connector) {
		if (connector.getAssemblyContext__RequiredInfrastructureDelegationConnector() == context
		        && connector.getInnerRequiredRole__RequiredInfrastructureDelegationConnector() == requiredRole) {
			return connector;
		}
		return null;
	}
}
