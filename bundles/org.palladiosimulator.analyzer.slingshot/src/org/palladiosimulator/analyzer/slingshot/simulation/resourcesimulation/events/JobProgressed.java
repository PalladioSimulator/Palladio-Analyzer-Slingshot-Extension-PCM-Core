package org.palladiosimulator.analyzer.slingshot.simulation.resourcesimulation.events;

import java.util.List;
import java.util.UUID;

import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.resourcesimulation.impl.Job;

public class JobProgressed implements DESEvent {

	
	private final Job job;
	private double delay;
	private double simulationTime;
	private String id;
	private UUID expectedResourceState;
	
	public JobProgressed(Job job, double delay, UUID expectedState) {
		this.job = job;
		this.delay = delay;
		this.id = UUID.randomUUID().toString();
		this.expectedResourceState = expectedState;
	}
	
	public UUID getExpectedResourceState() {
		return expectedResourceState;
	}

	public Job getJob() {
		return job;
	}
	
	
	@Override
	public String getId() {
		return id;
	}

	@Override
	public List<DESEvent> handle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getDelay() {
		return delay;
	}

	@Override
	public double time() {
		// TODO Auto-generated method stub
		return simulationTime;
	}

	@Override
	public void setTime(double time) {
		this.simulationTime = time;
	}

}
