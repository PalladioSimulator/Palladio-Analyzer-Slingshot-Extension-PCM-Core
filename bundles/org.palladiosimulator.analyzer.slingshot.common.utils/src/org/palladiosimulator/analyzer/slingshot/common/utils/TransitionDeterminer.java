package org.palladiosimulator.analyzer.slingshot.common.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.palladiosimulator.pcm.core.PCMRandomVariable;
import org.palladiosimulator.pcm.usagemodel.BranchTransition;
import com.google.common.base.Preconditions;
import de.uka.ipd.sdq.simucomframework.variables.StackContext;
import de.uka.ipd.sdq.simucomframework.variables.stackframe.SimulatedStackframe;

/**
 * Util class to determine a transition based on probabilities.
 * 
 * @author Joachim Meyer
 * @author Julijan Katic
 */
public class TransitionDeterminer {

	private static final Logger LOGGER = Logger.getLogger(TransitionDeterminer.class);

	/** The stack frame in which the parameter lies. */
	private final SimulatedStackframe<Object> currentStackFrame;

	public TransitionDeterminer(final SimulatedStackframe<Object> currentStackFrame) {
		Preconditions.checkNotNull(currentStackFrame);
		this.currentStackFrame = currentStackFrame;
	}

	/**
	 * Checks whether the boolean expression in the condition holds or not.
	 * 
	 * @param condition the condition that must be a boolean expression
	 * @return true iff it holds.
	 */
	private boolean conditionHolds(final PCMRandomVariable condition) {
		return StackContext.evaluateStatic(condition.getSpecification(), Boolean.class, this.currentStackFrame);
	}

	/**
	 * Sums the probabilities of the list of probabilities. In a list of summed
	 * probabilities, each value of an element in the list has its own probability
	 * added by the previous probability. Means, if the first probabilites in the
	 * list of probabilities is 0.3, the value of the first element in the summed
	 * probability list is 0.3. If the second probability in the list is 0.4, the
	 * corresponding value in the summed probability list is 0.4+0.3 and so on.
	 * 
	 * @param branchProbabilities a list with branch probabilities.
	 * @return the summed probability list.
	 */
	protected List<Double> createSummedProbabilityList(final List<Double> branchProbabilities) {
		double currentSum = 0;
		final List<Double> summedProbabilityList = new ArrayList<>();
		for (final Double probability : branchProbabilities) {
			currentSum += probability;
			summedProbabilityList.add(currentSum);
		}
		return summedProbabilityList;
	}

	public BranchTransition determineBranchTransition(final EList<BranchTransition> branchTransitions) {
		final List<Double> summedProbabilityList = this
		        .createSummedProbabilityList(this.extractProbabilitiesUsageModel(branchTransitions));
		final int transitionIndex = this.getRandomIndex(summedProbabilityList);

		return branchTransitions.get(transitionIndex);
	}

	private int getRandomIndex(final List<Double> summedProbabilityList) {
		if (summedProbabilityList.size() == 0) {
			return -1;
		}

		final double lastSum = summedProbabilityList.get(summedProbabilityList.size() - 1);
		final double randomNumber = Math.random(); // TODO: Use SimuCom Random instead

		int i = 0;
		for (final Double sum : summedProbabilityList) {
			if (lastSum * randomNumber < sum) {
				return i;
			}
			i++;
		}
		return -1;
	}

	protected List<Double> extractProbabilitiesUsageModel(final EList<BranchTransition> branchTransitions) {
		return branchTransitions.stream()
		        .map(branchTransition -> branchTransition.getBranchProbability())
		        .collect(Collectors.toList());
	}
}
