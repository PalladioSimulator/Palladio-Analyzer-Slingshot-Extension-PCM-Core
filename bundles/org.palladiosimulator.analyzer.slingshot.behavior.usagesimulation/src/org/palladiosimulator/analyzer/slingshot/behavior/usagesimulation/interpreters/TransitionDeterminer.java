package org.palladiosimulator.analyzer.slingshot.behavior.usagesimulation.interpreters;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.UsageInterpretationContext;
import org.palladiosimulator.pcm.core.PCMRandomVariable;
import org.palladiosimulator.pcm.seff.AbstractBranchTransition;
import org.palladiosimulator.pcm.usagemodel.BranchTransition;

import de.uka.ipd.sdq.simucomframework.variables.StackContext;
import de.uka.ipd.sdq.simucomframework.SimuComConfig;

public class TransitionDeterminer {

	// private final SimuComConfig config;

	public TransitionDeterminer(final UsageInterpretationContext context) {
		// this.config = context.
	}

	private boolean conditionHolds(final PCMRandomVariable condition) {
		return StackContext.evaluateStatic(condition.getSpecification(), Boolean.class);
	}

	protected List<Double> createSummedProbabilityList(final List<Double> branchProbabilities) {
		double currentSum = 0;
		final List<Double> summedProbabilityList = new ArrayList<>();
		for (final Double probability : branchProbabilities) {
			summedProbabilityList.add((currentSum += probability));
		}
		return summedProbabilityList;
	}

	public BranchTransition determineBranchTransition(final EList<BranchTransition> branchTransitions) {
		final List<Double> summedProbabilityList = this
		        .createSummedProbabilityList(this.extractProbabilitiesUsageModel(branchTransitions));
		final int transitionIndex = this.getRandomIndex(summedProbabilityList);

		final BranchTransition branchTransition = branchTransitions.get(transitionIndex);
		return branchTransition;
	}

	private int getRandomIndex(final List<Double> summedProbabilityList) {
		if (summedProbabilityList.size() == 0) {
			return -1;
		}
		
		final double lastSum = summedProbabilityList.get(summedProbabilityList.size() - 1);
		final double randomNumer = 
	}

	private List<Double> extractProbabilitiesUsageModel(final EList<BranchTransition> branchTransitions) {
		final List<Double> probabilityList = new ArrayList<>();
		for (final BranchTransition branchTransition : branchTransitions) {
			probabilityList.add(branchTransition.getBranchProbability());
		}
		return probabilityList;
	}

}
