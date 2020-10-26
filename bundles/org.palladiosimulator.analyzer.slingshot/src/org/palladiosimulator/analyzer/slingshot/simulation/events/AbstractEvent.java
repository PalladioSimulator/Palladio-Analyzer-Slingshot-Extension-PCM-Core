package org.palladiosimulator.analyzer.slingshot.simulation.events;

import java.util.UUID;

import com.google.common.base.Preconditions;

/**
 * Instead of directly implementing {@link DESEvent}, this abstract class
 * already gives a general implementation for such events. This holds an ID,
 * delay and time as fields that can be retrieved from the methods
 * {@link #getId()}, {@link #getDelay()} and {@link #getTime()} that were
 * defined in the {@link DESEvent} interface.
 * 
 * <p>
 * This class also provides a {@link #equals()} and {@link #hashCode()}
 * implementation that follow the following rule: Two {@link AbstractEvent}
 * instances are considered equal iff the corresponding {@link getId()} are
 * equal. It is therefore very important to define a unique and appropriate ID,
 * especially as this ID will be used for building the event graph. For this
 * reason, when extending this class, use the
 * {@link #AbstractEvent(Class, double)} or {@link #AbstractEvent(Class)}
 * constructor and provide the subclass' class literal as the parameter, i.e.
 * 
 * <pre>
 * <code>
 * public MyEvent extends AbstractEvent {
 *     public MyEvent(final double delay) {
 *         super(MyEvent.class, delay);
 *     }
 * }
 * </code>
 * </pre>
 * 
 * The convention for an ID is therefore the name of the event itself. This
 * means that each event should have its own corresponding class extending this
 * class.
 * 
 * @author Julijan Katic
 */
public abstract class AbstractEvent implements DESEvent {

	private final String id;
	private final double delay;
	private double time;

	/**
	 * Instantiates the class with default values. The id will be set to a random
	 * UUID ({@link UUID#randomUUID()}) and the delay will be set to 0.
	 */
	protected AbstractEvent() {
		this(0);
	}

	/**
	 * Instantiates this event with a random UUID as the id, and the sets the delay.
	 * 
	 * @param delay The delay of this event.
	 */
	protected AbstractEvent(final double delay) {
		this(UUID.randomUUID().toString(), delay);
	}

	/**
	 * Instantiates this event, where the id will be the simple name of the subclass
	 * extending this class.
	 * 
	 * @param subClazz The subclass' class literal that extends this abstract class.
	 * @param delay    The delay for this event.
	 */
	protected AbstractEvent(final Class<? extends AbstractEvent> subClazz, final double delay) {
		this(subClazz.getSimpleName(), delay);
	}

	/**
	 * Instantiates this event where the ID can be provided freely.
	 * 
	 * @param id    The id for this event. Must not be null nor blank.
	 * @param delay The delay of this event.
	 */
	protected AbstractEvent(final String id, final double delay) {
		Preconditions.checkArgument(id != null && !id.isBlank(), "The id must neither be null nor blank.");
		this.delay = delay;
		this.id = id;
		this.time = 0;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public double getDelay() {
		return delay;
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
		final AbstractEvent other = (AbstractEvent) obj;
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
