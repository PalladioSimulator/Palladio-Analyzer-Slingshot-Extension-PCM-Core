package org.palladiosimulator.analyzer.slingshot.simulation.resourcesimulation.events;

import java.util.List;

import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;

public class JobScheduled implements DESEvent {

	//TODO:: This should contain the meta-data for assigning it to a specific resource
	
	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DESEvent> handle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getDelay() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double time() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setTime(double time) {
		// TODO Auto-generated method stub
	}

}
