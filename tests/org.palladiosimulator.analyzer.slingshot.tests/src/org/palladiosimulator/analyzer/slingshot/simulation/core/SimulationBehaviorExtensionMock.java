package org.palladiosimulator.analyzer.slingshot.simulation.core;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.simulation.core.SimulationBehaviorExtensionTest.CallbackInLastEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.core.events.SampleEventA;
import org.palladiosimulator.analyzer.slingshot.simulation.core.events.SampleEventB;
import org.palladiosimulator.analyzer.slingshot.simulation.core.events.SampleEventC;
import org.palladiosimulator.analyzer.slingshot.simulation.core.events.SimulationStarted;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.SimulationBehaviorExtension;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.EventMethod;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.OnEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.results.ResultEvent;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

/**
 * This mocks a simulation behavior. This provides three sample events and has
 * the possibility to capture them.
 * 
 * @author Julijan Katic
 */
@OnEvent(when = SimulationStarted.class, then = SampleEventA.class)
@OnEvent(when = SampleEventA.class, then = SampleEventB.class)
@OnEvent(when = SampleEventB.class, then = SampleEventC.class)
@OnEvent(when = SampleEventC.class, then = {}) // No event afterwards
public class SimulationBehaviorExtensionMock implements SimulationBehaviorExtension {

	private final List<Class<?>> cachedEvents;

	private final Logger LOGGER = Logger.getLogger(SimulationBehaviorExtensionMock.class);

	/**
	 * The callback function to call at the last event.
	 */
	private final CallbackInLastEvent callback;

	@Inject
	public SimulationBehaviorExtensionMock(final CallbackInLastEvent callback) {
		this.cachedEvents = new ArrayList<>();
		this.callback = callback;
	}

	@Override
	public void init() {
		LOGGER.info("Init called");
	}

	@Subscribe
	public ResultEvent<SampleEventA> onFirstEvent(final SimulationStarted started) {
		cachedEvents.add(SimulationStarted.class);
		LOGGER.info("onFirstEvent called");
		return ResultEvent.of(new SampleEventA());
	}

	@EventMethod
	@Subscribe
	public ResultEvent<SampleEventB> sampleEventWithADifferentName(final SampleEventA sampleEventA) {
		cachedEvents.add(SampleEventA.class);
		LOGGER.info("sampleEventWithADifferentName called");
		return ResultEvent.of(new SampleEventB());
	}

	@Subscribe
	public ResultEvent<SampleEventC> onSampleEventB(final SampleEventB sampleEventB) {
		cachedEvents.add(SampleEventB.class);
		return ResultEvent.of(new SampleEventC());
	}

	@Subscribe
	public ResultEvent<DESEvent> onSampleEventC(final SampleEventC sampleEventC) {
		cachedEvents.add(SampleEventC.class);
		this.callback.accept(cachedEvents);
		return ResultEvent.empty();
	}

}
