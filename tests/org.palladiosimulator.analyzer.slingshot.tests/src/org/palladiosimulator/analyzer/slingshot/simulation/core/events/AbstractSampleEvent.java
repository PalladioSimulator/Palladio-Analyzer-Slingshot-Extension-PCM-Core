package org.palladiosimulator.analyzer.slingshot.simulation.core.events;

import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;

public abstract class AbstractSampleEvent implements DESEvent {

	private final String id;
	private double time;

	public AbstractSampleEvent(final String id) {
		this.id = id;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public double getDelay() {
		return 0;
	}

	@Override
	public double time() {
		return time;
	}

	@Override
	public void setTime(final double time) {
		this.time = time;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final AbstractSampleEvent other = (AbstractSampleEvent) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

}
