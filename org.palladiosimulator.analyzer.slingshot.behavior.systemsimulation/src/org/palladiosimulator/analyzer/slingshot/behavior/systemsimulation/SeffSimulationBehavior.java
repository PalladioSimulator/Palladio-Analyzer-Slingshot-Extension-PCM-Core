package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation;

import java.util.Set;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.seff.SEFFInterpretationContext;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.seff.behaviorcontext.ForkBehaviorContextHolder;
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
import org.palladiosimulator.analyzer.slingshot.common.events.AbstractSimulationEvent;
import org.palladiosimulator.analyzer.slingshot.core.extension.SimulationBehaviorExtension;
import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.Subscribe;
import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.eventcontract.EventCardinality;
import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.eventcontract.OnEvent;
import org.palladiosimulator.analyzer.slingshot.eventdriver.returntypes.Result;

/**
 * This behavior module both interprets and generates events specifically for
 * SEFFs.
 *
 * @author Julijan Katic, Floriment Klinaku, Sarah Stiess
 */
@OnEvent(when = SEFFInterpretationProgressed.class, then = SEFFInterpreted.class, cardinality = EventCardinality.MANY)
@OnEvent(when = SEFFInterpretationFinished.class, then = { SEFFInterpretationProgressed.class,
		UserRequestFinished.class }, cardinality = EventCardinality.SINGLE)
@OnEvent(when = SEFFChildInterpretationStarted.class, then = SEFFInterpreted.class, cardinality = EventCardinality.MANY)
@OnEvent(when = PassiveResourceAcquired.class, then = SEFFInterpreted.class, cardinality = EventCardinality.MANY)
public class SeffSimulationBehavior implements SimulationBehaviorExtension {

	private static final Logger LOGGER = Logger.getLogger(SeffSimulationBehavior.class);

	@Subscribe
	public Result<SEFFInterpreted> onSeffInterpretationProgressed(final SEFFInterpretationProgressed progressed) {
		final SeffInterpreter interpreter = new SeffInterpreter(progressed.getEntity());
		final Set<SEFFInterpreted> events = interpreter
				.doSwitch(progressed.getEntity().getBehaviorContext().getNextAction());
		return Result.of(events);
	}


	@Subscribe
	public Result<SEFFInterpreted> onPassiveResourceAcquired(final PassiveResourceAcquired passiveResourceAcquired) {
		final SeffInterpreter interpreter = new SeffInterpreter(
				passiveResourceAcquired.getEntity().getSeffInterpretationContext());
		final Set<SEFFInterpreted> events = interpreter.doSwitch(passiveResourceAcquired.getEntity()
				.getSeffInterpretationContext().getBehaviorContext().getNextAction());
		return Result.of(events);
	}

	@Subscribe
	public Result<SEFFInterpreted> onSEFFChildInterpretationStarted(
			final SEFFChildInterpretationStarted seffChildInterpretationStarted) {
		final SeffInterpreter interpreter = new SeffInterpreter(seffChildInterpretationStarted.getEntity());
		final Set<SEFFInterpreted> events = interpreter
				.doSwitch(seffChildInterpretationStarted.getEntity().getBehaviorContext().getNextAction());
		return Result.of(events);
	}

	@Subscribe
	public Result<AbstractSimulationEvent> onSEFFInterpretationFinished(final SEFFInterpretationFinished finished) {
		final SEFFInterpretationContext entity = finished.getEntity();
		final Result<AbstractSimulationEvent> result;
		/*
		 * If the interpretation is finished in a SEFF that was called from another
		 * SEFF, continue there. Otherwise, the SEFF comes from a User request.
		 */
		if (entity.getBehaviorContext() instanceof ForkBehaviorContextHolder) {

			final ForkBehaviorContextHolder fb = (ForkBehaviorContextHolder) entity.getBehaviorContext();

			if (!entity.getBehaviorContext().hasFinished()) {
				LOGGER.info("A forked behavior has finished, but not all");
				result = Result.of();
			} else if (fb.isProcessed()) {
				result = Result.of();
			} else {
				LOGGER.info("return to parent - from forked");
				fb.markProcessed();
				result = Result.of(this.continueInParent(entity));
			}
		} else if (!entity.getBehaviorContext().hasFinished()) {
			LOGGER.info("repeat scenario");
			result = Result.of(this.repeat(entity));
		} else if (entity.getCaller().isPresent()) {
			LOGGER.info("return to caller");
			result = Result.of(this.continueInCaller(entity));
		} else if (entity.getBehaviorContext().isChild()) {
			LOGGER.info("return to parent");
			result = Result.of(this.continueInParent(entity));
		} else {
			LOGGER.info("finish request");
			result = Result.of(this.finishUserRequest(entity));
		}

		return result;
	}

	/**
	 * @param entity
	 * @return
	 */
	private UserRequestFinished finishUserRequest(final SEFFInterpretationContext entity) {
		final UserRequest userRequest = entity.getRequestProcessingContext().getUserRequest();
		final UserInterpretationContext userInterpretationContext = entity.getRequestProcessingContext()
				.getUserInterpretationContext();

		entity.getRequestProcessingContext().getUser().getStack().removeStackFrame();

		return new UserRequestFinished(userRequest, userInterpretationContext);
	}

	/**
	 * @param entity
	 * @return
	 */
	private SEFFInterpretationProgressed continueInParent(final SEFFInterpretationContext entity) {

		final SeffBehaviorWrapper seffBehaviorHolder = entity.getBehaviorContext().getParent().get();

		final SEFFInterpretationContext seffInterpretationContext = SEFFInterpretationContext.builder()
				.withAssemblyContext(entity.getAssemblyContext())
				.withBehaviorContext(seffBehaviorHolder.getContext())
				.withCaller(entity.getCaller())
				.withRequestProcessingContext(entity.getRequestProcessingContext())
				.build();

		return new SEFFInterpretationProgressed(seffInterpretationContext);
	}

	/**
	 * @param entity
	 * @return
	 */
	private SEFFInterpretationProgressed continueInCaller(final SEFFInterpretationContext entity) {
		final SEFFInterpretationContext seffInterpretationContext = entity.getCaller().get();

		return new SEFFInterpretationProgressed(seffInterpretationContext);
	}

	private SEFFInterpretationProgressed repeat(final SEFFInterpretationContext entity) {
		return new SEFFInterpretationProgressed(entity);
	}
}
