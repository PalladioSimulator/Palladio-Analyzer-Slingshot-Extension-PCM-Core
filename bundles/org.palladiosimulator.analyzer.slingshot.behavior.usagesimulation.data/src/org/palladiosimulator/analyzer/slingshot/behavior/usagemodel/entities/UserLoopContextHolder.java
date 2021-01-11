package org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities;

import javax.annotation.processing.Generated;

import org.palladiosimulator.pcm.usagemodel.AbstractUserAction;
import org.palladiosimulator.pcm.usagemodel.Start;

/**
 * An immutable class for holding information for a certain loop action. This
 * will give information about the number of loops and the current loop count
 * (progression).
 * 
 * @author Julijan Katic
 */
public final class UserLoopContextHolder {

	/** The (maximum) number of loops to be reached. */
	private final int numberOfLoops;

	/** The current loop count. */
	private final int progression;

	/** The first action of the loop's inner scenario behavior. */
	private final Start loopStartAction;

	/** The successor action of the loop. */
	private final AbstractUserAction afterLoopAction;

	/**
	 * Value holding whether the loop is finished. This value will be true if
	 * {@code progression >= numberOfLoops}
	 */
	private final boolean hasLoopFinished;

	private final UserInterpretationContext context;

	@Generated("SparkTools")
	private UserLoopContextHolder(final Builder builder) {
		this.numberOfLoops = builder.numberOfLoops;
		this.progression = builder.progression;
		this.loopStartAction = builder.loopStartAction;
		this.afterLoopAction = builder.afterLoopAction;
		this.hasLoopFinished = this.progression >= this.numberOfLoops;
		this.context = builder.context;
	}

	/**
	 * Returns the maximum number of loops to be reached.
	 * 
	 * @return maximum number of loops.
	 */
	public int getNumberOfLoops() {
		return numberOfLoops;
	}

	/**
	 * Returns the current loop count.
	 * 
	 * @return current loop count.
	 */
	public int getProgression() {
		return progression;
	}

	/**
	 * Returns the start action of inner scenario behavior from the loop action.
	 * 
	 * @return the start action.
	 */
	public Start getLoopStartAction() {
		return loopStartAction;
	}

	/**
	 * Increments the current loop count and returns the new instance.
	 * 
	 * @return the new instance with the updated loop count.
	 */
	public UserLoopContextHolder progress() {
		return this.update().withProgression(progression + 1).build();
	}

	/**
	 * Returns whether the loop has finished. The loop is considered finished if the
	 * current loop count is greater or equal to the maximum number of loops.
	 * 
	 * @return true iff current loop count {@code >=} number of loops.
	 */
	public boolean hasLoopFinished() {
		return hasLoopFinished;
	}

	/**
	 * Returns the action that comes after the loop action.
	 * 
	 * @return successor action of the loop.
	 */
	public AbstractUserAction getAfterLoopAction() {
		return afterLoopAction;
	}

	public Builder update() {
		return builder().withAfterLoopAction(afterLoopAction)
		        .withContext(context)
		        .withLoopStartAction(loopStartAction)
		        .withNumberOfLoops(numberOfLoops)
		        .withProgression(progression);
	}

	/**
	 * Creates builder to build {@link UserLoopContextHolder}.
	 * 
	 * @return created builder
	 */
	@Generated("SparkTools")
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Builder to build {@link UserLoopContextHolder}.
	 */
	@Generated("SparkTools")
	public static final class Builder {
		private int numberOfLoops;
		private int progression;
		private Start loopStartAction;
		private AbstractUserAction afterLoopAction;
		private UserInterpretationContext context;

		private Builder() {
		}

		public Builder withNumberOfLoops(final int numberOfLoops) {
			this.numberOfLoops = numberOfLoops;
			return this;
		}

		public Builder withProgression(final int progression) {
			this.progression = progression;
			return this;
		}

		public Builder withLoopStartAction(final Start loopStartAction) {
			this.loopStartAction = loopStartAction;
			return this;
		}

		public Builder withAfterLoopAction(final AbstractUserAction afterLoopAction) {
			this.afterLoopAction = afterLoopAction;
			return this;
		}

		public Builder withContext(final UserInterpretationContext context) {
			this.context = context;
			return this;
		}

		public UserLoopContextHolder build() {
			return new UserLoopContextHolder(this);
		}
	}

}
