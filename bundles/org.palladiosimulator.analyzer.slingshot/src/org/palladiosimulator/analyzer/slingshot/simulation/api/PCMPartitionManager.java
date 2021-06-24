package org.palladiosimulator.analyzer.slingshot.simulation.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import java.util.Optional;

import javax.inject.Qualifier;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.palladiosimulator.analyzer.workflow.ConstantsContainer;
import org.palladiosimulator.analyzer.workflow.blackboard.PCMResourceSetPartition;
import org.palladiosimulator.commons.emfutils.EMFCopyHelper;
import org.palladiosimulator.monitorrepository.MonitorRepositoryPackage;

import de.uka.ipd.sdq.workflow.mdsd.blackboard.MDSDBlackboard;

/* Taken from https://github.com/PalladioSimulator/Palladio-Analyzer-SimuLizar/blob/dc25ea5ba6ddb983102b02b4a43192e5dab54069/bundles/org.palladiosimulator.simulizar/src/org/palladiosimulator/simulizar/utils/PCMPartitionManager.java#L45 */
/**
 * The class manages all the models that are required during simulation. This
 * includes querying for specific models as well as observing and handling model
 * changes.
 * 
 * @author scheerer
 *
 */
public final class PCMPartitionManager {
	/**
	 * The Global annotation should be used to reference to the global
	 * PCMResourceSet partition for constructor injection.
	 * 
	 * The global partition should always be used to apply persistent changes to the
	 * simulation model, i.e. by reconfiguration. Any model interpretetation (i.e.
	 * visitor-based interpreters) should not use the global partition, but use the
	 * local copy, which guarantees a stable view.
	 */
	@Qualifier
	@Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD })
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Global {
	}

	/**
	 * The Local annotation should be used to reference to the PCMResourceSet
	 * partition of the current interpretation scope for constructor injection.
	 * 
	 * The local partition should always be used for model interpretation.
	 */
	@Qualifier
	@Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD })
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Local {
	}

	private static final Logger LOGGER = Logger.getLogger(PCMPartitionManager.class);

	private final static String RM_MODEL_FILE_EXTENSION_STRING = ".runtimemeasurement";

	private final PCMResourceSetPartition globalPartition;
	private final MDSDBlackboard blackboad;
	private final boolean isObservingPcmChanges = false;
	private PCMResourceSetPartition currentPartition;
	private boolean modelsDirty = false;

	/**
	 * The constructor initializes the blackboard, which is the primary source to
	 * manage, makes copies of the current PCM related mode, which may be changed by
	 * reconfigurations. Moreover, a runtime measurement model is created and
	 * temporarily persisted (this is necessary for keeping it in the blackboard)
	 * until simulation is done.
	 * 
	 * @param blackboard The workflow engine's blackboard holding all models.
	 */
	public PCMPartitionManager(final MDSDBlackboard blackboard) {
		this.blackboad = blackboard;
		this.globalPartition = (PCMResourceSetPartition) blackboard
				.getPartition(ConstantsContainer.DEFAULT_PCM_INSTANCE_PARTITION_ID);
		if (this.globalPartition == null) {
			throw new IllegalStateException("The provided blackboard does not contain the required PCM partition!");
		}
		this.currentPartition = this.copyPCMPartition();
	}

	public void initialize() {
		final Optional<EObject> result = this.globalPartition
				.getElement(MonitorRepositoryPackage.Literals.MONITOR_REPOSITORY).stream().findAny();
		if (result.isPresent()) {
//			var uri = result.get().eResource().getURI().appendFileExtension(RM_MODEL_FILE_EXTENSION_STRING);
//			Resource resource = this.globalPartition.getResourceSet().createResource(uri);
//			resource.getContents().add(RuntimeMeasurementFactory.eINSTANCE.createRuntimeMeasurementModel());
		} else {
			LOGGER.error("No monitor repository set in global partition.");
		}
	}

	/**
	 * @return the global PCM modeling partition. The global PCM model is the
	 *         primary model under change, e.g., whenever a reconfiguration is
	 *         triggered, and is observed during simulation.
	 */
	public PCMResourceSetPartition getGlobalPCMModel() {
		return this.globalPartition;
	}

	/**
	 * @return a copy of the global PCM modeling partition. The local PCM model
	 *         represents an up-to-date snapshot of the global PCM model that
	 *         captures the latest changes made in the global PCM model.
	 */
	public PCMResourceSetPartition getLocalPCMModel() {
		this.checkAndHandleDeferredChanges();
		return this.currentPartition;
	}

	private void checkAndHandleDeferredChanges() {
		if (this.modelsDirty) {
			this.currentPartition = this.copyPCMPartition();
			this.modelsDirty = false;
		}
	}

	/**
	 * @return a copy of the global PCM modeling partition
	 */
	private PCMResourceSetPartition copyPCMPartition() {
		LOGGER.debug("Take a new copy of the global PCM for new simulation threads");
		final PCMResourceSetPartition newPartition = new PCMResourceSetPartition();
		final List<EObject> modelCopy = EMFCopyHelper.deepCopyToEObjectList(this.globalPartition.getResourceSet());
		for (int i = 0; i < modelCopy.size(); i++) {
			final Resource resource = newPartition.getResourceSet()
					.createResource(URI.createFileURI("/temp" + i));
			resource.getContents()
					.add(modelCopy.get(i));
		}
		return newPartition;
	}

	/**
	 * Enables to query the blackboard for a specific model that has been stored.
	 * 
	 * @param targetType Corresponds to the EClass of the target model to be
	 *                   searched for.
	 * @return the model to search for or null if the model was not found.
	 */
	public <T extends EObject> T findModel(final EClass targetType) {
		final List<T> resultList = this.globalPartition.getElement(targetType);
		if (resultList.isEmpty()) {
			LOGGER.info(String.format("No model with target type %s was found.", targetType));
			return null;
		}
		return resultList.get(0);
	}
}
