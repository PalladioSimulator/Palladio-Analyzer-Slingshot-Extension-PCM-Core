package org.palladiosimulator.analyzer.slingshot.simulation.resourcesimulation.impl;

import java.util.UUID;

import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.SimulatedUser;

public class Job {


	private final String id;
	private int priority;
	private Job rootProcess;
	private boolean finished;
	private final SimulatedUser user;
	private double demand;
	
	public double getDemand() {
		return demand;
	}

	public void setDemand(double demand) {
		this.demand = demand;
	}

	public Job(int priority, Job rootProcess, boolean finished, SimulatedUser user, double demand) {
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
	
	public SimulatedUser getUser() {
		return user;
	}

}
