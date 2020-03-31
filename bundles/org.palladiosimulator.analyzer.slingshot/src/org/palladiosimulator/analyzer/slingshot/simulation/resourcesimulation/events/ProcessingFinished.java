package org.palladiosimulator.analyzer.slingshot.simulation.resourcesimulation.events;

import java.util.List;
import java.util.UUID;

import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.resourcesimulation.impl.ResourceProcess;

public class ProcessingFinished implements DESEvent {

	
	private final ResourceProcess process;
	private double delay;
	private double simulationTime;
	private String id;
	
	public ProcessingFinished(ResourceProcess process, double delay) {
		this.process = process;
		this.delay = delay;
		this.id = UUID.randomUUID().toString();
	}
	
	public ResourceProcess getProcess() {
		return process;
	}
	
	
	@Override
	public String getId() {
		return id;
	}

	@Override
	public List<DESEvent> handle() {
		// TODO Auto-generated method stub
//       toNow();
//       assert MathTools.equalsDouble(0, running_processes.get(first)) : "Remaining demand ("
//               + running_processes.get(first) + ") not zero!";
//       running_processes.remove(first);
//       processQ.remove(first);
//       fireStateChange(processQ.size(), 0);
//       fireDemandCompleted(first);
//       LoggingWrapper.log("Demand of Process " + first + " finished.");
//       scheduleNextEvent();
//       first.activate();
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
