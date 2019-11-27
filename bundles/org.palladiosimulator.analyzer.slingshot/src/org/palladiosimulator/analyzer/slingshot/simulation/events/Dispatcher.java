package org.palladiosimulator.analyzer.slingshot.simulation.events;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class Dispatcher {
	
	// list of events that were once scheduled and their event routine was successfully executed
	List<DESEvent> finishedEvents;
	
	
	// list of event observers  
	List<EventObserver> eventObservers;
	
	private final Logger LOGGER = Logger.getLogger(Dispatcher.class);

	
	public Dispatcher() {
		finishedEvents = new ArrayList<DESEvent>();
		eventObservers = new ArrayList<EventObserver>();
	}
	
	public void addFinishedEvent(DESEvent evt) {
		
		finishedEvents.add(evt);
		
		// filter all the observers which are listening for changes on the given event type 
		// FIXME:: Here the question lies if an Observer listens for DESEvent will it receive the notification for all
		// and whether we need to 
		
		// on a finished event there might be several extension mechanisms that are interested to listen:
		// e.g., monitoring, self-adaptations, runtime state etc. Therefore we need to think whether first 
		// priorities make sense here. 
		
		eventObservers.stream().filter(x -> !x.equals(null)).forEach(evOb -> evOb.update(evt));
	}
	
	public void addObserver(EventObserver eventObserver) {
		eventObservers.add(eventObserver);
	}
	
	public void removeObserver(EventObserver eventObserver) {
		eventObservers.remove(eventObserver);
		
	}

}
