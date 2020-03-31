package org.palladiosimulator.analyzer.slingshot.simulation.resourcesimulation.impl;

import java.util.UUID;

import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.SimulatedUser;

public class ResourceProcess {


	private final String id;
	private int priority;
	private ResourceProcess rootProcess;
	private boolean finished;
	private final SimulatedUser user;
	private double demand;
	
	public double getDemand() {
		return demand;
	}

	public void setDemand(double demand) {
		this.demand = demand;
	}

	public ResourceProcess(int priority, ResourceProcess rootProcess, boolean finished, SimulatedUser user, double demand) {
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

	public ResourceProcess getRootProcess() {
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
