package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation;

import java.util.Set;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.seff.SEFFInterpretationContext;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.seff.behaviorcontext.SeffBehaviorWrapper;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.PassiveResourceAcquired;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.SEFFChildInterpretationStarted;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.SEFFInterpretationFinished;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.SEFFInterpretationProgressed;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.SEFFInterpreted;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.interpreters.SeffInterpreter;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.UserRequest;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.interpretationcontext.UserInterpretationContext;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UserRequestFinished;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.SimulationBehaviorExtension;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.EventCardinality;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.OnEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.results.ResultEvent;

import com.google.common.eventbus.Subscribe;

/**
 * This behavior module both interprets and generates events specifically for
 * SEFFs.
 * 
 * @author Julijan Katic
 */
@OnEvent(when = SEFFInterpretationProgressed.class, then = SEFFInterpreted.class, cardinality = EventCardinality.MANY)
@OnEvent(when = SEFFInterpretationFinished.class, then = { SEFFInterpretationProgressed.class,
		UserRequestFinished.class }, cardinality = EventCardinality.SINGLE)
@OnEvent(when = SEFFChildInterpretationStarted.class, then = SEFFInterpreted.class, cardinality = EventCardinality.MANY)
@OnEvent(when = PassiveResourceAcquired.class, then=SEFFInterpreted.class, cardinality = EventCardinality.MANY)
public class SeffSimulationBehavior implements SimulationBehaviorExtension {

	private static final Logger LOGGER = Logger.getLogger(SeffSimulationBehavior.class);

	@Subscribe
	public ResultEvent<SEFFInterpreted> onSeffInterpretationProgressed(final SEFFInterpretationProgressed progressed) {
		final SeffInterpreter interpreter = new SeffInterpreter(progressed.getEntity());
		final Set<SEFFInterpreted> events = interpreter
				.doSwitch(progressed.getEntity().getBehaviorContext().getNextAction());
		return ResultEvent.of(events);
	}
	
	@Subscribe
	public ResultEvent<SEFFInterpreted> onPassiveResourceAcquired(final PassiveResourceAcquired passiveResourceAcquired){
		final SeffInterpreter interpreter = new SeffInterpreter(passiveResourceAcquired.getEntity().getSeffInterpretationContext());
		final Set<SEFFInterpreted> events = interpreter
				.doSwitch(passiveResourceAcquired.getEntity().getSeffInterpretationContext().getBehaviorContext().getNextAction());
		return ResultEvent.of(events);
	}

	
	@Subscribe
	public ResultEvent<SEFFInterpreted> onSEFFChildInterpretationStarted(
			final SEFFChildInterpretationStarted seffChildInterpretationStarted) {
		final SeffInterpreter interpreter = new SeffInterpreter(seffChildInterpretationStarted.getEntity());
		final Set<SEFFInterpreted> events = interpreter
				.doSwitch(seffChildInterpretationStarted.getEntity().getBehaviorContext().getNextAction());
		return ResultEvent.of(events);
	}

	@Subscribe
	public ResultEvent<DESEvent> onSEFFInterpretationFinished(final SEFFInterpretationFinished finished) {
		final SEFFInterpretationContext entity = finished.getEntity();
		final ResultEvent<DESEvent> result;
		/*
		 * If the interpretation is finished in a SEFF that was called from another
		 * SEFF, continue there. Otherwise, the SEFF comes from a User request.
		 */
		if (!entity.getBehaviorContext().hasFinished()) {
			LOGGER.info("repeat scenario");
			result = this.repeat(entity);
		} else if (entity.getCaller().isPresent()) {
			LOGGER.info("return to caller");
			result = this.continueInCaller(entity);
		} else if (entity.getBehaviorContext().isChild()) {
			LOGGER.info("return to parent");
			result = this.continueInParent(entity);
		} else {
			LOGGER.info("finish request");
			result = this.finishUserRequest(entity);
		}

		return result;
	}

	/**
	 * @param entity
	 * @return
	 */
	private ResultEvent<DESEvent> finishUserRequest(final SEFFInterpretationContext entity) {
		final UserRequest userRequest = entity.getRequestProcessingContext().getUserRequest();
		final UserInterpretationContext userInterpretationContext = entity.getRequestProcessingContext()
				.getUserInterpretationContext();

		entity.getRequestProcessingContext().getUser().getStack().removeStackFrame();

		return ResultEvent.of(new UserRequestFinished(userRequest, userInterpretationContext));
	}

	/**
	 * @param entity
	 * @return
	 */
	private ResultEvent<DESEvent> continueInParent(final SEFFInterpretationContext entity) {
		final SeffBehaviorWrapper seffBehaviorHolder = entity.getBehaviorContext().getParent().get();

		final SEFFInterpretationContext seffInterpretationContext = SEFFInterpretationContext.builder()
				.withAssemblyContext(entity.getAssemblyContext()).withBehaviorContext(seffBehaviorHolder.getContext())
				.withCaller(entity.getCaller()).withRequestProcessingContext(entity.getRequestProcessingContext())
				.build();

		return ResultEvent.of(new SEFFInterpretationProgressed(seffInterpretationContext));
	}

	/**
	 * @param entity
	 * @return
	 */
	private ResultEvent<DESEvent> continueInCaller(final SEFFInterpretationContext entity) {
		final SEFFInterpretationContext seffInterpretationContext = entity.getCaller().get();

		return ResultEvent.of(new SEFFInterpretationProgressed(seffInterpretationContext));
	}

	private ResultEvent<DESEvent> repeat(final SEFFInterpretationContext entity) {
		return ResultEvent.of(new SEFFInterpretationProgressed(entity));
	}
}
