package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation;

import static org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.EventCardinality.MANY;
import static org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.EventCardinality.SINGLE;

import java.util.Set;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.GeneralEntryRequest;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.RepositoryInterpretationContext;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.UserEntryRequest;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.RepositoryInterpretationInitiated;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.RequestFinished;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.RequestInitiated;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.UserEntryRequested;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.seffspecificevents.SeffInterpretationRequested;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.interpreters.RepositoryInterpreter;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.repository.SystemModelRepository;
import org.palladiosimulator.analyzer.slingshot.common.utils.SimulatedStackHelper;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.SimulationBehaviorExtension;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.OnEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.results.ResultEvent;
import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;

import com.google.common.eventbus.Subscribe;

/**
 * The System simulation behavior is a extension that simulates the system
 * model. It listens to events requesting to interpret the repository and
 * sometimes will result in a seff intepretation request if there is a RDSeff.
 * 
 * @author Julijan Katic
 */
@OnEvent(when = UserEntryRequested.class, then = RepositoryInterpretationInitiated.class, cardinality = SINGLE)
@OnEvent(when = RepositoryInterpretationInitiated.class, then = SeffInterpretationRequested.class, cardinality = MANY)
@OnEvent(when = RequestInitiated.class, then = SeffInterpretationRequested.class, cardinality = MANY)
@OnEvent(when = RequestFinished.class, then = {})
public class SystemSimulationBehavior implements SimulationBehaviorExtension {

	private final Logger LOGGER = Logger.getLogger(SystemSimulationBehavior.class);

	private final Allocation allocationModel;
	private final SystemModelRepository systemRepository;

	@Inject
	public SystemSimulationBehavior(final Allocation allocationModel, final SystemModelRepository repository) {
		this.allocationModel = allocationModel;
		this.systemRepository = repository;
	}

	@Override
	public void init() {
		this.systemRepository.load(allocationModel.getSystem_Allocation());
	}

	/**
	 * Handles the UserEntryRequest event by starting the interpretation of the
	 * repository. The goal is to find the right SEFF and then interpreting that.
	 * 
	 * @param userEntryRequested the event.
	 * @return RepositoryInterpretationInitiated event if found.
	 */
	@Subscribe
	public ResultEvent<DESEvent> onUserEnterRequest(final UserEntryRequested userEntryRequested) {
		final UserEntryRequest entryRequest = userEntryRequested.getEntity();

		SimulatedStackHelper.createAndPushNewStackFrame(entryRequest.getUser().getStack(), entryRequest.getVariableUsages(),
		        entryRequest.getUser().getStack().currentStackFrame());

		final RepositoryInterpretationContext repositoryContext = new RepositoryInterpretationContext(
		        entryRequest.getUser());
		repositoryContext.setSignature(entryRequest.getSignature());
		repositoryContext.setProvidedRole(entryRequest.getProvidedRole());
		repositoryContext.setInputParameters(entryRequest.getVariableUsages());

		return ResultEvent.of(new RepositoryInterpretationInitiated(repositoryContext, 0));
	}

	/**
	 * This event will handle the repository interpretation of a system, especially
	 * for the entry of a system.
	 */
	@Subscribe
	public ResultEvent<SeffInterpretationRequested> onRepositoryInterpretationInitiated(
	        final RepositoryInterpretationInitiated event) {
		final RepositoryInterpretationContext context = event.getEntity();

		final RepositoryInterpreter interpreter = new RepositoryInterpreter(context.getAssemblyContext(),
		        context.getSignature(), context.getProvidedRole(), context.getUser(), this.systemRepository);
		final Set<SeffInterpretationRequested> appearedEvents = interpreter.doSwitch(context.getProvidedRole());

		return ResultEvent.of(appearedEvents);
	}

	@Subscribe
	public ResultEvent<SeffInterpretationRequested> onRequestInitiated(final RequestInitiated requestInitiated) {
		final GeneralEntryRequest entity = requestInitiated.getEntity();
		final AssemblyContext assemblyContext = systemRepository
		        .findAssemblyContextFromRequiredRole(entity.getRequiredRole());

		final RepositoryInterpreter interpreter = new RepositoryInterpreter(assemblyContext, entity.getSignature(),
		        null, entity.getUser(), systemRepository);

		/* Interpret the Component of the system. */
		final Set<SeffInterpretationRequested> appearedEvents = interpreter
		        .doSwitch(assemblyContext.getEncapsulatedComponent__AssemblyContext());

		return ResultEvent.of(appearedEvents);
	}

	/**
	 * Handles the event that a seff interpretation is finished, either from a user
	 * or from a general event. In both cases the last stackframe is popped as there
	 * shouldn't be any further calculations.
	 * 
	 * @return An empty event set.
	 */
	@Subscribe
	public ResultEvent<DESEvent> onRequestFinished(final RequestFinished requestFinished) {

		requestFinished.getEntity().getStack().removeStackFrame();

		return ResultEvent.empty();
	}
}
