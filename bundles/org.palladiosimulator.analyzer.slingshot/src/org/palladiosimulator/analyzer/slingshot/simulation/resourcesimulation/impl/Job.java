package org.palladiosimulator.analyzer.slingshot.simulation.resourcesimulation.impl;

import java.util.UUID;

import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.User;

public class Job {


	private final String id;
	private int priority;
	private Job rootProcess;
	private boolean finished;
	private final User user;
	private double demand;
	
	public double getDemand() {
		return demand;
	}

	public void setDemand(double demand) {
		this.demand = demand;
	}

	public Job(int priority, Job rootProcess, boolean finished, User user, double demand) {
		this.id = UUID.randomUUID().toString();
		this.priority = priority;
		this.rootProcess = rootProcess;
		this.finished = finished;
		this.user = user;
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
	
	public User getUser() {
		return user;
	}

}
