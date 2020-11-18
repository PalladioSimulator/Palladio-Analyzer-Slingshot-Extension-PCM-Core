package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.Request;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.SeffInterpretationEntity;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.RequestFinished;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.RequestInitiated;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.SeffInterpretationRequested;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.UserRequest;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UserRequestFinished;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UserRequestInitiated;
import org.palladiosimulator.analyzer.slingshot.repositories.SystemModelRepository;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.SimulationBehaviorExtension;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.EventCardinality;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.OnEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.results.ResultEvent;
import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.core.composition.ProvidedDelegationConnector;
import org.palladiosimulator.pcm.repository.ProvidedRole;
import org.palladiosimulator.pcm.repository.Signature;
import org.palladiosimulator.pcm.seff.AbstractAction;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;
import org.palladiosimulator.pcm.seff.ServiceEffectSpecification;

import com.google.common.eventbus.Subscribe;

@OnEvent(when = RequestInitiated.class, then = SeffInterpretationRequested.class, cardinality = EventCardinality.MANY)
@OnEvent(when = RequestFinished.class, then = UserRequestFinished.class, cardinality = EventCardinality.SINGLE)
@OnEvent(when = UserRequestInitiated.class, then = RequestInitiated.class, cardinality = EventCardinality.SINGLE)
public class SystemSimulationBehavior implements SimulationBehaviorExtension {

	private final Logger LOGGER = Logger.getLogger(SystemSimulationBehavior.class);

	private Map<Request, RequestInterpretationContext> requestContexts;

	private final Allocation allocationModel;
	private final SystemModelRepository systemRepository;

	private RequestInterpretationContext requestInterpretationContext;
	private SeffInterpretationContext seffInterpretationContext;

	@Inject
	public SystemSimulationBehavior(final Allocation allocationModel, final SystemModelRepository repository) {
		this.allocationModel = allocationModel;
		this.systemRepository = repository;
	}

	@Override
	public void init() {
		requestContexts = new HashMap<Request, RequestInterpretationContext>();
		this.systemRepository.load(allocationModel.getSystem_Allocation());
		requestInterpretationContext = new RequestInterpretationContext();
		seffInterpretationContext = new SeffInterpretationContext();
	}

	@Subscribe
	public ResultEvent<DESEvent> onUserRequestInitiated(final UserRequestInitiated userRequestInit) {

		requestInterpretationContext.setUserInterpretationContext(userRequestInit.getUserContext());
		final Request request = new Request();
		requestContexts.put(request, requestInterpretationContext);

		request.setProvidedRole(userRequestInit.getEntity().getOperationProvidedRole());
		request.setSignature(userRequestInit.getEntity().getOperationSignature());
		request.setUser(userRequestInit.getEntity().getUser());

		return ResultEvent.of(new RequestInitiated(request, 0));
	}

	@SuppressWarnings("unchecked")
	@Subscribe
	public ResultEvent<UserRequestFinished> onRequestFinished(final RequestFinished requestFinishedEvt) {

		// get the request
		final Request request = requestFinishedEvt.getEntity();
		final UserRequest userReq = new UserRequest(null, null, null);

		return ResultEvent
		        .of(new UserRequestFinished(userReq, requestContexts.get(request).getUserInterpretationContext()));
	}

	@Subscribe
	public ResultEvent<DESEvent> onRequestInitiated(final RequestInitiated requestInitiatedEvt) {
		final Request request = requestInitiatedEvt.getEntity();

		final ProvidedRole providedRole = request.getProvidedRole();
		final Signature signature = request.getSignature();
		final ProvidedDelegationConnector connector = systemRepository
		        .getConnectedProvidedDelegationConnector(providedRole);
		final ServiceEffectSpecification spec = systemRepository.getDelegatedComponentSeff(connector, signature);

		return ResultEvent.of(interpreteSeff(spec));
	}

	/**
	 * Interpretes a single {@code ServiceEffectSpecification} and makes spawns for
	 * the first actions different events.
	 * 
	 * @param spec The service effect specification model.
	 * @return A set with {@link SeffInterpretationRequested} event if spec is a
	 *         ResourceDemandingSEFF, otherwise an empty set.
	 */
	private Set<DESEvent> interpreteSeff(final ServiceEffectSpecification spec) {
		if (spec instanceof ResourceDemandingSEFF) {
			final ResourceDemandingSEFF rdSpec = (ResourceDemandingSEFF) spec;
			final AbstractAction firstAction = rdSpec.getSteps_Behaviour().get(0);
			final SeffInterpretationEntity seffContext = new SeffInterpretationEntity(firstAction);

			LOGGER.info("Found Service Effect Specification: " + spec.getSeffTypeID());

			return Set.of(new SeffInterpretationRequested(seffContext, 0));
		}

		return Set.of();
	}

	/**
	 * Returns a list of certain SEFFs that correspond to a signature.
	 * 
	 * @param serviceEffectSpecifications The list of all SEFFs
	 * @param signature                   The signature to compare to.
	 * @return The list of SEFFs corresponding to the signature.
	 */
	private List<ServiceEffectSpecification> getSeffsForCall(
	        final EList<ServiceEffectSpecification> serviceEffectSpecifications, final Signature signature) {
		final List<ServiceEffectSpecification> specs = new ArrayList<>();
		for (final ServiceEffectSpecification spec : serviceEffectSpecifications) {
			if (spec.getDescribedService__SEFF().getId().equals(signature.getId())) {
				specs.add(spec);
			}
		}
		return specs;
	}
}
