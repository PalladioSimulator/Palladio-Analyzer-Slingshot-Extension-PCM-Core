package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.resources.active;

import java.util.HashSet;
import java.util.Set;

import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.jobs.Job;

public class DelayResource extends AbstractActiveResource {

	private final Set<Job> runningJobs;

	public DelayResource(final String id, final String name) {
		super(id, name, INFINITE_CAPACITY);
		this.runningJobs = new HashSet<>();
	}

	@Override
	public void enqueue(final Job job) {
		this.runningJobs.add(job);
	}

	@Override
	public boolean isEmpty() {
		return this.runningJobs.isEmpty();
	}

	@Override
	public void process(final double elapsedTime) {
		this.runningJobs.forEach(job -> {
			this.runningJobs.remove(job);
			this.notify(job);
		});
	}

	@Override
	public void cancelJob(final Job job) {
		this.runningJobs.remove(job);
	}

}
