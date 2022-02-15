package org.palladiosimulator.analyzer.slingshot.scalingpolicy.interpreter;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.palladiosimulator.analyzer.slingshot.common.serialization.load.PCMFileLoader;
import org.palladiosimulator.pcm.resourceenvironment.ResourceEnvironment;

import spd.targetgroup.TargetGroup;

public final class TargetGroupTable {

	private static final TargetGroupTable INSTANCE = new TargetGroupTable();

	private final Map<TargetGroup, ResourceEnvironment> resourceEnvironment = new HashMap<>();

	private TargetGroupTable() {
	}

	public ResourceEnvironment getEnvironment(final TargetGroup group) {
		if (this.resourceEnvironment.containsKey(group)) {
			return this.resourceEnvironment.get(group);
		}

		final EObject object = PCMFileLoader.load(Paths.get(group.getResourceEnvironment_ID()));

		if (object instanceof ResourceEnvironment) {
			this.resourceEnvironment.put(group, (ResourceEnvironment) object);
			return (ResourceEnvironment) object;
		}

		throw new IllegalArgumentException("Currently, only resource environments are supported");
	}

	public static TargetGroupTable instance() {
		return INSTANCE;
	}
}
