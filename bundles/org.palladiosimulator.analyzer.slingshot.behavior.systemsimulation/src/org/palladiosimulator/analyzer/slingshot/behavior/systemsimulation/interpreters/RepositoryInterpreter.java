package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.interpreters;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.SeffInterpretationEntity;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.seffspecificevents.SeffInterpretationRequested;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.User;
import org.palladiosimulator.analyzer.slingshot.common.utils.SimulatedStackHelper;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.core.composition.ComposedStructure;
import org.palladiosimulator.pcm.core.composition.CompositionPackage;
import org.palladiosimulator.pcm.core.composition.Connector;
import org.palladiosimulator.pcm.core.composition.ProvidedDelegationConnector;
import org.palladiosimulator.pcm.core.entity.ComposedProvidingRequiringEntity;
import org.palladiosimulator.pcm.core.entity.EntityPackage;
import org.palladiosimulator.pcm.core.entity.InterfaceProvidingEntity;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.repository.ProvidedRole;
import org.palladiosimulator.pcm.repository.Signature;
import org.palladiosimulator.pcm.repository.util.RepositorySwitch;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;
import org.palladiosimulator.pcm.seff.ServiceEffectSpecification;

import de.uka.ipd.sdq.simucomframework.variables.stackframe.SimulatedStackframe;

public class RepositoryInterpreter extends RepositorySwitch<Set<SeffInterpretationRequested>> {

	private static final Logger LOGGER = Logger.getLogger(RepositoryInterpreter.class);

	private final AssemblyContext assemblyContext;
	private final ProvidedRole providedRole;
	private final Signature signature;
	private final User user;

	public RepositoryInterpreter(final AssemblyContext context, final Signature signature,
	        final ProvidedRole providedRole, final User user) {
		this.assemblyContext = context;
		this.signature = signature;
		this.providedRole = providedRole;
		this.user = user;
	}

	public RepositoryInterpreter(final Signature signature, final ProvidedRole providedRole, final User user) {
		this(null, signature, providedRole, user);
	}

	/**
	 * Spawns {@link SeffInterpretationRequested} events with the provided
	 * {@link #signature}.
	 */
	@Override
	public Set<SeffInterpretationRequested> caseBasicComponent(final BasicComponent object) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Entering BasicComponent: " + object);
		}

		final SimulatedStackframe<Object> componentParameterStackframe = SimulatedStackHelper
		        .createAndPushNewStackFrame(this.user.getStack(),
		                object.getComponentParameterUsage_ImplementationComponentType(),
		                this.user.getStack().currentStackFrame());
		SimulatedStackHelper.createAndPushNewStackFrame(this.user.getStack(),
		        this.assemblyContext.getConfigParameterUsages__AssemblyContext(), componentParameterStackframe);

		final List<ServiceEffectSpecification> calledSeffs = this
		        .getSeffsForCall(object.getServiceEffectSpecifications__BasicComponent(), this.signature);

		/*
		 * Define for each SEFF a new request event to be interpreted.
		 */
		return calledSeffs.stream()
		        .filter(seff -> seff instanceof ResourceDemandingSEFF)
		        .map(seff -> (ResourceDemandingSEFF) seff)
		        .map(rdSeff -> {
			        final SeffInterpretationEntity entity = new SeffInterpretationEntity(assemblyContext, user,
			                rdSeff.getSteps_Behaviour().get(0));
			        return new SeffInterpretationRequested(entity, 0);
		        })
		        .collect(Collectors.toSet());

	}

	private List<ServiceEffectSpecification> getSeffsForCall(
	        final EList<ServiceEffectSpecification> serviceEffectSpecifications,
	        final Signature operationSignature) {
		assert serviceEffectSpecifications != null && operationSignature != null;
		return serviceEffectSpecifications.stream()
		        .filter(seff -> seff.getDescribedService__SEFF().getId().equals(operationSignature.getId()))
		        .collect(Collectors.toList());
	}

	/**
	 * Interprets the provided role of a system model / repository model. This is
	 * done by looking at the providing entity and looking which events can be
	 * spawned by it.
	 * 
	 * If the providedRole belongs to a composed entity (such as the system as a
	 * whole where the user can enter the system), then
	 * {@link #caseComposedProvidingRequiringEntity()} will be called. Otherwise,
	 * the normal {@link #caseBasicComponent()} will be called.
	 */
	@Override
	public Set<SeffInterpretationRequested> caseProvidedRole(final ProvidedRole providedRole) {
		LOGGER.debug("Accessing provided role: " + providedRole.getId());
		/*
		 * TODO: Why is here a null pointer exception now? getProvidingEntity()
		 * is always null, even if it is from a composed structure
		 */
		return this.doSwitch(providedRole.getProvidingEntity_ProvidedRole());
	}

	@Override
	public Set<SeffInterpretationRequested> caseComposedProvidingRequiringEntity(
	        final ComposedProvidingRequiringEntity entity) {

		if (entity != this.providedRole.getProvidingEntity_ProvidedRole()) {
			/*
			 * Interpret entity of provided role only.
			 */
			return Set.of();
		}

		final ProvidedDelegationConnector connectedProvidedDelegationConnector = getConnectedProvidedDelegationConnector();
		final RepositoryInterpreter repositoryInterpreter = new RepositoryInterpreter(
		        connectedProvidedDelegationConnector.getAssemblyContext_ProvidedDelegationConnector(), this.signature,
		        connectedProvidedDelegationConnector.getInnerProvidedRole_ProvidedDelegationConnector(), this.user);
		return repositoryInterpreter
		        .doSwitch(connectedProvidedDelegationConnector.getInnerProvidedRole_ProvidedDelegationConnector());
	}

	/**
	 * Determines the provided delegation connector which is connected with the
	 * provided role.
	 * 
	 * @return the determined provided delegation connector, null otherwise.
	 */
	private ProvidedDelegationConnector getConnectedProvidedDelegationConnector() {
		final InterfaceProvidingEntity implementingEntity = providedRole.getProvidingEntity_ProvidedRole();
		for (final Connector connector : ((ComposedStructure) implementingEntity).getConnectors__ComposedStructure()) {
			if (connector.eClass() == CompositionPackage.eINSTANCE.getProvidedDelegationConnector()) {
				final ProvidedDelegationConnector delegationConnector = (ProvidedDelegationConnector) connector;
				if (delegationConnector.getOuterProvidedRole_ProvidedDelegationConnector().equals(providedRole)) {
					return delegationConnector;
				}
			}
		}
		return null;
	}

	/**
	 * Overrides the switch in such a way that
	 * {@link ComposedProvidingRequiringEntity}s (and their subtypes) will always
	 * call {@link #caseComposedProvidingRequiringEntity()}. Also, it ensures that
	 * never {@code null} is returned, but an empty set instead.
	 */
	@Override
	public Set<SeffInterpretationRequested> doSwitch(final EClass eClass, final EObject eObject) {
		Set<SeffInterpretationRequested> result;
		if (EntityPackage.eINSTANCE.getComposedProvidingRequiringEntity().isSuperTypeOf(eClass)) {
			result = this.caseComposedProvidingRequiringEntity((ComposedProvidingRequiringEntity) eObject);
		} else {
			result = super.doSwitch(eClass, eObject);
		}

		if (result == null) {
			result = Set.of();
		}

		return result;
	}
}
