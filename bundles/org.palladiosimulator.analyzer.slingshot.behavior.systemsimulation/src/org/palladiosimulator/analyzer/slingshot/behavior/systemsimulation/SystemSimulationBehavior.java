package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation;

import static org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.EventCardinality.MANY;
import static org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.EventCardinality.SINGLE;

import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.GeneralEntryRequest;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.RepositoryInterpretationContext;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.seff.SEFFInterpretationContext;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.seff.behaviorcontext.RootBehaviorContextHolder;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.user.RequestProcessingContext;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.RepositoryInterpretationInitiated;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.SEFFExternalActionCalled;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.SEFFInterpretationProgressed;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.interpreters.RepositoryInterpreter;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.repository.SystemModelRepository;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.UserRequest;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UserEntryRequested;
import org.palladiosimulator.analyzer.slingshot.common.utils.SimulatedStackHelper;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.SimulationBehaviorExtension;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.OnEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.results.ResultEvent;
import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.parameter.VariableUsage;
import org.palladiosimulator.pcm.repository.OperationProvidedRole;
import org.palladiosimulator.pcm.repository.OperationSignature;
import org.palladiosimulator.pcm.seff.ResourceDemandingBehaviour;
import org.palladiosimulator.pcm.seff.ServiceEffectSpecification;

import com.google.common.eventbus.Subscribe;

/**
 * The System simulation behavior is a extension that simulates the system
 * model. It listens to events requesting to interpret the repository and
 * sometimes will result in a SEFF Interpretation request if there is a RDSeff.
 * 
 * @author Julijan Katic
 */
@OnEvent(when = UserEntryRequested.class, then = SEFFInterpretationProgressed.class, cardinality = SINGLE)
@OnEvent(when = RepositoryInterpretationInitiated.class, then = SEFFInterpretationProgressed.class, cardinality = MANY)
@OnEvent(when = SEFFExternalActionCalled.class, then = SEFFInterpretationProgressed.class, cardinality = MANY)
public class SystemSimulationBehavior implements SimulationBehaviorExtension {

	private static final Logger LOGGER = Logger.getLogger(SystemSimulationBehavior.class);

	private final Allocation allocationModel;
	private final SystemModelRepository systemRepository;

	@Inject
	public SystemSimulationBehavior(final Allocation allocationModel, final SystemModelRepository repository) {
		this.allocationModel = allocationModel;
		this.systemRepository = repository;
	}

	@Override
	public void init() {
		this.systemRepository.load(this.allocationModel.getSystem_Allocation());
	}

	/**
	 * Used to interpret the entry request from a usage model.
	 */
	@Subscribe
	public ResultEvent<DESEvent> onUserEntryRequested(final UserEntryRequested userEntryRequested) {
		final UserRequest request = userEntryRequested.getEntity();

		final OperationProvidedRole operationProvidedRole = request.getOperationProvidedRole();
		final OperationSignature operationSignature = request.getOperationSignature();
		final EList<VariableUsage> variableUsages = request.getVariableUsages();
		
		/* Receive the assembly context and its seff. */
		final Optional<ServiceEffectSpecification> seffFromProvidedRole = this.systemRepository.getSeffFromProvidedRole(operationProvidedRole, operationSignature);
		final Optional<AssemblyContext> assemblyContextByProvidedRole = this.systemRepository.findAssemblyContextByProvidedRole(operationProvidedRole);
		
		
		if (seffFromProvidedRole.isPresent() && assemblyContextByProvidedRole.isPresent()) {
			SimulatedStackHelper.createAndPushNewStackFrame(request.getUser().getStack(), variableUsages);	
			final ServiceEffectSpecification seff = seffFromProvidedRole.get();
			
			assert seff instanceof ResourceDemandingBehaviour;
			
			final RequestProcessingContext requestProcessingContext = RequestProcessingContext.builder()
				.withUser(request.getUser())
				.withUserRequest(request)
				.withUserInterpretationContext(userEntryRequested.getUserInterpretationContext())
				.withProvidedRole(operationProvidedRole)
				.withAssemblyContext(assemblyContextByProvidedRole.get())
				.build();
			
			final SEFFInterpretationContext context = SEFFInterpretationContext.builder()
				.withRequestProcessingContext(requestProcessingContext)
				.withAssemblyContext(assemblyContextByProvidedRole.get())
				.withBehaviorContext(new RootBehaviorContextHolder((ResourceDemandingBehaviour) seff))
				.build();
			
			return ResultEvent.of(new SEFFInterpretationProgressed(context));
		} else {
			LOGGER.info("Either seff or assembly context is not found => stop interpretation for this request.");
		}
		
		return ResultEvent.of();
	}

	/**
	 * This event will handle the repository interpretation of a system, especially
	 * for the entry of a system.
	 * 
	 * TODO: Is this needed?
	 */
	@Subscribe
	public ResultEvent<SEFFInterpretationProgressed> onRepositoryInterpretationInitiated(
			final RepositoryInterpretationInitiated event) {
		final RepositoryInterpretationContext context = event.getEntity();

		final RepositoryInterpreter interpreter = new RepositoryInterpreter(context.getAssemblyContext(),
				context.getSignature(), context.getProvidedRole(), context.getUser(), this.systemRepository);
		final Set<SEFFInterpretationProgressed> appearedEvents = interpreter.doSwitch(context.getProvidedRole());

		return ResultEvent.of(appearedEvents);
	}

	/**
	 * Used to interpret the next SEFF that is requested by another seff. For example, when an External Call
	 * action was performed.
	 */
	@Subscribe
	public ResultEvent<SEFFInterpretationProgressed> onRequestInitiated(final SEFFExternalActionCalled requestInitiated) {
		final GeneralEntryRequest entity = requestInitiated.getEntity();
		final Optional<AssemblyContext> assemblyContext = this.systemRepository
				.findAssemblyContextFromRequiredRole(entity.getRequiredRole());

		if (assemblyContext.isPresent()) {
			final RepositoryInterpreter interpreter = new RepositoryInterpreter(assemblyContext.get(), entity.getSignature(),
					null, entity.getUser(), this.systemRepository);
	
			/* Interpret the Component of the system. */
			final Set<SEFFInterpretationProgressed> appearedEvents = interpreter
					.doSwitch(assemblyContext.get().getEncapsulatedComponent__AssemblyContext());
	
			return ResultEvent.of(appearedEvents);
		} else {
			return ResultEvent.of();
		}
	}

}
