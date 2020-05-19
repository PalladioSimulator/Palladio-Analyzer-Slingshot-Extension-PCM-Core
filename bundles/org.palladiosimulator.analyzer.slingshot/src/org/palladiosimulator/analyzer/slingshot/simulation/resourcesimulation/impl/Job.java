package org.palladiosimulator.analyzer.slingshot.simulation.resourcesimulation.impl;

import java.util.UUID;

import org.palladiosimulator.analyzer.slingshot.simulation.systemsimulation.impl.Request;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.entities.User;

public class Job {


	private final String id;
	private int priority;
	private Job rootProcess;
	private boolean finished;
	private double demand;

	private final Request request;
	
	public double getDemand() {
		return demand;
	}

	public void setDemand(double demand) {
		this.demand = demand;
	}

	public Job(int priority, Job rootProcess, boolean finished, double demand, Request request) {
		this.request = request;
		this.id = UUID.randomUUID().toString();
		this.priority = priority;
		this.rootProcess = rootProcess;
		this.finished = finished;
		this.demand = demand;
	}

	public String getId() {
		// TODO Auto-generated method stub
		return id;
	}

	public int getPriority() {
		// TODO Auto-generated method stub
		return priority;
	}

	public Job getRootProcess() {
		// TODO Auto-generated method stub
		return rootProcess;
	}

	public boolean isFinished() {
		// TODO Auto-generated method stub
		return finished;
	}

	public Request getRequest() {
		return request;
	}
	

}
