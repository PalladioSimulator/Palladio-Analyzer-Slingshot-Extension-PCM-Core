package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities;

import java.util.UUID;

import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.Request;

public class Job {

	private final String id;
	private final int priority;
	private final Job rootProcess;
	private final boolean finished;
	private double demand;
	
	private final Request request;

	public Job(final int priority, final Job rootProcess, final boolean finished, final double demand, final Request request) {
		super();
		this.priority = priority;
		this.rootProcess = rootProcess;
		this.finished = finished;
		this.demand = demand;
		this.request = request;
		
		this.id = UUID.randomUUID().toString();
	}

	public double getDemand() {
		return demand;
	}

	public void setDemand(final double demand) {
		this.demand = demand;
	}

	public String getId() {
		return id;
	}

	public int getPriority() {
		return priority;
	}

	public Job getRootProcess() {
		return rootProcess;
	}

	public boolean isFinished() {
		return finished;
	}

	public Request getRequest() {
		return request;
	}

	
	
}
