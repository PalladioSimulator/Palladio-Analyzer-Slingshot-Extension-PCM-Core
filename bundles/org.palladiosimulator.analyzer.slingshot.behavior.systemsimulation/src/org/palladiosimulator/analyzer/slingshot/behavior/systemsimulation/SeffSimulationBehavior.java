package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation;

import java.util.Set;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.ActiveResourceRequestContext;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.ActiveResourceRequested;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.JobInitiated;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.seffspecificevents.ResourceDemandRequestInitiated;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.seffspecificevents.SeffInterpretationEvent;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.seffspecificevents.SeffInterpretationRequested;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.interpreters.SeffInterpreter;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.SimulationBehaviorExtension;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.EventCardinality;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.OnEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.results.ResultEvent;

import com.google.common.eventbus.Subscribe;

import de.uka.ipd.sdq.simucomframework.variables.StackContext;

/**
 * This behavior module both interprets and generates events specifically for
 * SEFFs.
 * 
 * @author Julijan Katic
 */
@OnEvent(when = SeffInterpretationRequested.class, then = SeffInterpretationEvent.class, cardinality = EventCardinality.MANY)
@OnEvent(when = ResourceDemandRequestInitiated.class, then = ActiveResourceRequested.class, cardinality = EventCardinality.SINGLE)
public class SeffSimulationBehavior implements SimulationBehaviorExtension {

	private final Logger LOGGER = Logger.getLogger(SeffSimulationBehavior.class);

	@Subscribe
	public ResultEvent<?> onSeffInterpretationStarted(final SeffInterpretationRequested event) {
		final SeffInterpreter seffInterpreter = new SeffInterpreter(event.getEntity().getAssemblyContext(),
		        event.getEntity().getUser());
		final Set<DESEvent> events = seffInterpreter.doSwitch(event.getEntity().getCurrentAction());
		return ResultEvent.of(events);
	}

	/**
	 * This catches the event if the SEFF calls an internal method with a certain
	 * ResourceDemand. It behaves by resulting an {@link JobInitiated} event to for
	 * the resource simulation.
	 */
	@Subscribe
	public ResultEvent<?> onResourceDemandRequestInitiated(final ResourceDemandRequestInitiated event) {
		final Double resourceDemand = StackContext.evaluateStatic(event.getEntity().getDemand().getSpecification(),
		        Double.class, event.getEntity().getUser().getStack().currentStackFrame());
		final ActiveResourceRequestContext activeResourceRequestContext = new ActiveResourceRequestContext(
		        event.getEntity().getRequiredResource(), event.getEntity().getAssemblyContext(), resourceDemand);

		return ResultEvent.of(new ActiveResourceRequested(activeResourceRequestContext, 0));
	}
}
