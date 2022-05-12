package org.palladiosimulator.analyzer.slingshot.scalingpolicy.interpreter;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.palladiosimulator.analyzer.slingshot.common.serialization.load.PCMFileLoader;
import org.palladiosimulator.pcm.resourceenvironment.ResourceEnvironment;

import spd.palladio.PCMResourceEnvironment;
import spd.targetgroup.TargetGroup;

/**
 * This class holds all the necessary target groups and their respective
 * {@link ResourceEnvironment} found in the SPD in memory. This class is a singleton.
 * 
 * @author Julijan Katic
 *
 */
public final class TargetGroupTable {

	private static final TargetGroupTable INSTANCE = new TargetGroupTable();

	/** A cache holding all the resource environment instances. */
	private final Map<TargetGroup, ResourceEnvironment> resourceEnvironment = new HashMap<>();

	private TargetGroupTable() {
	}

	/**
	 * Returns and caches the {@link ResourceEnvironment} instance from the specific target group.
	 * If the resource environment does not exist, or if the resource ID specified in the
	 * target group is not of {@code ResourceEnvironment}, then {@code IllegalArgumentException}
	 * is thrown, since only ResourceEnvironments are currently supported.
	 * 
	 * @param group The target group to load the resource environment from.
	 * @return The resource environment instance, non-{@code null}.
	 * @throws IllegalArgumentException if {@link TargetGroup#getName()} does not
	 * point to a {@link ResourceEnvironment}.
	 */
	public ResourceEnvironment getEnvironment(final TargetGroup group) {
		if (this.resourceEnvironment.containsKey(group)) {
			return this.resourceEnvironment.get(group);
		}
		final PCMResourceEnvironment pcmResourceEnvironment = group.getPCM_ResourceEnvironment();
		
		if (pcmResourceEnvironment == null) {
			throw new NullPointerException("PCM resource environment is somehow null!! WTF");
		}
		
		final String resourceEnvironmentId = group.getPCM_ResourceEnvironment().getName();

		final EObject object = PCMFileLoader.load(Paths.get(resourceEnvironmentId));

		if (object instanceof ResourceEnvironment) {
			final ResourceEnvironment resourceEnvironment = (ResourceEnvironment) object;
			this.resourceEnvironment.put(group, resourceEnvironment);
			return resourceEnvironment;
		}

		throw new IllegalArgumentException("Currently, only resource environments are supported");
	}

	/**
	 * Returns the single instance of this class.
	 * @return A non-null instance of this class.
	 */
	public static TargetGroupTable instance() {
		return INSTANCE;
	}
}
