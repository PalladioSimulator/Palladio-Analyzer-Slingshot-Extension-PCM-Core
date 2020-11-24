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
import org.palladiosimulator.analyzer.slingshot.repositories.SystemModelRepository;
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

/**
 * The repository interpreter interprets either a repository or the system model
 * itself. It is based of a Switch which can be used to further iterate the
 * system. The repository interpreter sometimes needs a system model repository
 * (accessor) in order to get further information on the system, such as when a
 * provided role must be accessed.
 * <p>
 * This kind of interpreter will always return a set of events (or an empty
 * set). This interpreter is therefore made for behavior extensions in mind.
 * 
 * 
 * @author Julijan Katic
 */
public class RepositoryInterpreter extends RepositorySwitch<Set<SeffInterpretationRequested>> {

	private static final Logger LOGGER = Logger.getLogger(RepositoryInterpreter.class);

	/** The special assembly context to interpret. */
	private final AssemblyContext assemblyContext;

	/** The provided role from which the interpretation started. */
	private final ProvidedRole providedRole;

	/** A signature of to find the right RDSeff. */
	private final Signature signature;

	/** The context onto which to push stack frames for RDSeffs. */
	private final User user;

	/** The model repository to get more information from the system model. */
	private final SystemModelRepository modelRepository;

	/**
	 * Instantiates the interpreter with given information. Depending on the
	 * interpretation, not every parameter must be set (every parameter CAN be
	 * null!).
	 * 
	 * @param context         The special assembly context to interpret.
	 * @param signature       A signature to find the right RDSeff.
	 * @param providedRole    The provided role from which the interpretation
	 *                        started.
	 * @param user            The context onto which to push stack frames for
	 *                        RDSeffs.
	 * @param modelRepository The model repository to get more information from the
	 *                        system model.
	 */
	public RepositoryInterpreter(final AssemblyContext context, final Signature signature,
	        final ProvidedRole providedRole, final User user, final SystemModelRepository modelRepository) {
		this.assemblyContext = context;
		this.signature = signature;
		this.providedRole = providedRole;
		this.user = user;
		this.modelRepository = modelRepository;
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

	/**
	 * Helper method that returns the list of SEFFs that are meant for the
	 * operationSignature.
	 * 
	 * @param serviceEffectSpecifications The (Ecore) list of all seffs.
	 * @param operationSignature          The signature which a SEFF should
	 *                                    describe.
	 * @return List of seffs describing {@code operationSignature}.
	 */
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

		/* Sometime the providing entity is not defined and therefore must be
		 * found by the system model repository to find the right entity. 
		 */
		if (providedRole.getProvidingEntity_ProvidedRole() == null) {
			LOGGER.debug("ProvidedRole does not have the information about its providing entity, find it...");
			final InterfaceProvidingEntity foundEntity = this.modelRepository.findProvidingEntity(providedRole);
			providedRole.setProvidingEntity_ProvidedRole(foundEntity);
		}

		return this.doSwitch(providedRole.getProvidingEntity_ProvidedRole());
	}

	/**
	 * The ComposedProvidingRequiringEntity is a special entity (which is typically
	 * the system itself). It often has inner assembly contexts which is connected
	 * to this entity with a delegation connector.
	 * 
	 * If such assembly context exists, then the (provided) role of that assembly
	 * context will be interpreted.
	 * 
	 */
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
		        connectedProvidedDelegationConnector.getInnerProvidedRole_ProvidedDelegationConnector(), this.user,
		        this.modelRepository);
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
				if (delegationConnector.getOuterProvidedRole_ProvidedDelegationConnector().getId()
				        .equals(providedRole.getId())) {
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