package org.palladiosimulator.analyzer.slingshot.simulation.core;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.simulation.api.Simulation;
import org.palladiosimulator.analyzer.slingshot.simulation.core.events.SimulationStarted;
import org.palladiosimulator.analyzer.slingshot.simulation.engine.SimulationEngine;
import org.palladiosimulator.analyzer.slingshot.simulation.engine.SimulationEngineMock;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.events.EventObserver;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.events.UserFinished;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.events.UserStarted;
import org.palladiosimulator.pcm.usagemodel.UsageModel;

import com.google.common.eventbus.Subscribe;

public class SimulationDriver implements Simulation, SimulationScheduling {
	
	private final Logger LOGGER = Logger.getLogger(SimulationDriver.class);
	
	
	//FIXME: Remove the dependency to usageSimulation. 
	private SimulationEngine simEngine;
	
	private List<SimulationBehaviourExtension> behaviourExtensions;
	
	public SimulationDriver(final SimulationEngine simEngine) {
		this.simEngine = simEngine;
		this.behaviourExtensions = new ArrayList<SimulationBehaviourExtension>();
	}
	
	public SimulationDriver(final SimulationEngine simEngine, List<SimulationBehaviourExtension> simulationBehaviorExtensions) {
		this.simEngine = simEngine;
		this.behaviourExtensions = new ArrayList<SimulationBehaviourExtension>();
		this.behaviourExtensions.addAll(simulationBehaviorExtensions);
	}

	public void init(final UsageModel usageModel) {
		LOGGER.info("Start simulation driver initialization.");
		
		//code that glues the extensions with the core
		for (SimulationBehaviourExtension simulationBehaviourExtension : behaviourExtensions) {
			simulationBehaviourExtension.init(usageModel,this);
			this.simEngine.getEventDispatcher().register(simulationBehaviourExtension);
		}
		
		this.simEngine.getEventDispatcher().register(this);
		
		LOGGER.info("Finished simulation driver initialization.");
	}
	
	public void startSimulation() {
		DESEvent simulationStart = new SimulationStarted();
		simEngine.scheduleEvent(simulationStart);
		simEngine.start();
	}
	
	

	/**
	 * @return
	 */
	public SimulationMonitoring monitorSimulation() {
		//FIXME what would be now the Status.
		return new SimulationStatus(null);
	}

	

	

	@Subscribe public void update(DESEvent evt) {		
		//FIXME:: Check from which of the interested types the event is then delegate to usageSimulation to find the nextEvent and schedule that event.
		//FIXME:: The FinishedUserEvent is scheduled somewhere from the component in the last action which will be interpreted somewhere

		if(evt instanceof UserStarted) {
			UserStarted startUserEvent = UserStarted.class.cast(evt);
			LOGGER.info(String.format("Previously scheduled event '%s' has finished executing its event routine now we could schedule a FinishUserEvent", startUserEvent.getId()));
			UserFinished userFinished = new UserFinished(startUserEvent.getSimulatedUser());
			simEngine.scheduleEvent(userFinished);
			
		}

	}

	@Override
	public void scheduleForSimulation(DESEvent evt) {
		//all the events are scheduled through this public available method of the simulation driver
		LOGGER.info(String.format("SimulationDriver is advised to schedule a new event '%s",evt.getId()));
		
		simEngine.scheduleEvent(evt);
		
	}
	
	//another convenient method would be scheduleForSimulation of several events
	public void scheduleForSimulation(List<DESEvent> evt) {
		LOGGER.info(String.format("SimulationDriver is advised to schedule a list of events: '%s",evt));

		for (DESEvent desEvent : evt) {
			simEngine.scheduleEvent(desEvent);
		}
		
	}
	
}

