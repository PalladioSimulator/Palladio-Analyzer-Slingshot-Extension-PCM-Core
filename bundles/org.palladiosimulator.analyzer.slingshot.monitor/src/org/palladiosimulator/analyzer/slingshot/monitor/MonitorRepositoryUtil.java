package org.palladiosimulator.analyzer.slingshot.monitor;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EObject;
import org.palladiosimulator.commons.emfutils.EMFLoadHelper;
import org.palladiosimulator.edp2.models.measuringpoint.MeasuringPoint;
import org.palladiosimulator.edp2.models.measuringpoint.ResourceURIMeasuringPoint;
import org.palladiosimulator.edp2.models.measuringpoint.StringMeasuringPoint;
import org.palladiosimulator.edp2.models.measuringpoint.util.MeasuringpointSwitch;
import org.palladiosimulator.edp2.util.MetricDescriptionUtility;
import org.palladiosimulator.metricspec.MetricDescription;
import org.palladiosimulator.monitorrepository.MeasurementSpecification;
import org.palladiosimulator.monitorrepository.Monitor;
import org.palladiosimulator.monitorrepository.MonitorRepository;
import org.palladiosimulator.pcm.repository.PassiveResource;
import org.palladiosimulator.pcm.repository.util.RepositorySwitch;
import org.palladiosimulator.pcm.resourceenvironment.ProcessingResourceSpecification;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;
import org.palladiosimulator.pcm.resourceenvironment.ResourceEnvironment;
import org.palladiosimulator.pcm.resourceenvironment.util.ResourceenvironmentSwitch;
import org.palladiosimulator.pcm.seff.ExternalCallAction;
import org.palladiosimulator.pcm.seff.util.SeffSwitch;
import org.palladiosimulator.pcm.usagemodel.EntryLevelSystemCall;
import org.palladiosimulator.pcm.usagemodel.UsageScenario;
import org.palladiosimulator.pcm.usagemodel.util.UsagemodelSwitch;
import org.palladiosimulator.pcmmeasuringpoint.ActiveResourceMeasuringPoint;
import org.palladiosimulator.pcmmeasuringpoint.AssemblyOperationMeasuringPoint;
import org.palladiosimulator.pcmmeasuringpoint.AssemblyPassiveResourceMeasuringPoint;
import org.palladiosimulator.pcmmeasuringpoint.EntryLevelSystemCallMeasuringPoint;
import org.palladiosimulator.pcmmeasuringpoint.ExternalCallActionMeasuringPoint;
import org.palladiosimulator.pcmmeasuringpoint.ResourceContainerMeasuringPoint;
import org.palladiosimulator.pcmmeasuringpoint.ResourceEnvironmentMeasuringPoint;
import org.palladiosimulator.pcmmeasuringpoint.SystemOperationMeasuringPoint;
import org.palladiosimulator.pcmmeasuringpoint.UsageScenarioMeasuringPoint;
import org.palladiosimulator.pcmmeasuringpoint.util.PcmmeasuringpointSwitch;

/* Taken from https://github.com/PalladioSimulator/Palladio-Analyzer-SimuLizar/blob/master/bundles/org.palladiosimulator.simulizar/src/org/palladiosimulator/simulizar/utils/MonitorRepositoryUtil.java */
/**
 * Util methods for the monitoring model.
 * 
 * @author Steffen Becker
 * @author Sebastian Lehrig
 * @author Matthias Becker
 */
public final class MonitorRepositoryUtil {

	public static MeasurementSpecification isMonitored(final MonitorRepository monitorRepositoryModel,
			final EObject element, final MetricDescription metricDescription) {
		if (monitorRepositoryModel != null) {
			return monitorRepositoryModel.getMonitors().stream()
					.filter(monitor -> elementConformingToMeasuringPoint(element, monitor.getMeasuringPoint()))
					.flatMap(monitor -> monitor.getMeasurementSpecifications().stream())
					.filter(m -> MetricDescriptionUtility.metricDescriptionIdsEqual(m.getMetricDescription(),
							metricDescription))
					.findFirst()
					.orElse(null);
		}
		return null;
	}

	/**
	 * Method returns the monitored element EObject for a measuring point.
	 * 
	 * @param mp the measuring point for which the monitored element shall be
	 *           returned.
	 * @return the monitored element.
	 */
	public static EObject getMonitoredElement(final MeasuringPoint mp) {
		EObject eObject = getEObjectFromPCMMeasuringPoint(mp);

		if (eObject == null) {
			eObject = getEObjectFromSimulizarMeasuringPoint(mp);
			if (eObject == null) {
				eObject = getEObjectFromMeasuringPoint(mp);
				if (eObject == null) {
					throw new IllegalArgumentException("Could not find EObject for MeasuringPoint \""
							+ mp.getStringRepresentation() + "\" -- most likely this type of measuring point is "
							+ "not yet implemented within in getEObjectFromPCMMeasuringPoint "
							+ "or getEObjectFromGeneralMeasuringPoint methods.");
				}
			}
		}

		return eObject;
	}

	/**
	 * Returns the measured element EObject for a SimuLizar measuring point.
	 * 
	 * @param measuringPoint the measuring point.
	 * @return the measured element.
	 */
	private static EObject getEObjectFromSimulizarMeasuringPoint(final MeasuringPoint measuringPoint) {
//		return new SimulizarmeasuringpointSwitch<EObject>() {
//			
//			@Override
//			public EObject caseReconfigurationMeasuringPoint(final ReconfigurationMeasuringPoint object) {
//				return EMFLoadHelper.loadAndResolveEObject(object.getResourceURI());
//			}
//			
//		}.doSwitch(measuringPoint);
		return null;
	}

	/**
	 * Returns the measured element EObject for a general measuring point.
	 * 
	 * @param measuringPoint the measuring point.
	 * @return the measured element.
	 */
	private static EObject getEObjectFromMeasuringPoint(final MeasuringPoint measuringPoint) {
		return new MeasuringpointSwitch<EObject>() {

			@Override
			public EObject caseResourceURIMeasuringPoint(final ResourceURIMeasuringPoint object) {
				return EMFLoadHelper.loadAndResolveEObject(object.getResourceURI());
			}

		}.doSwitch(measuringPoint);
	}

	/**
	 * Returns the measured element EObject for a pre-defined PCM measuring point.
	 * 
	 * @param measuringPoint the measuring point.
	 * @return the measured element.
	 */
	private static EObject getEObjectFromPCMMeasuringPoint(final MeasuringPoint measuringPoint) {
		return new PcmmeasuringpointSwitch<EObject>() {

			@Override
			public EObject caseActiveResourceMeasuringPoint(final ActiveResourceMeasuringPoint object) {
				return object.getActiveResource();
			}

			@Override
			public EObject caseEntryLevelSystemCallMeasuringPoint(final EntryLevelSystemCallMeasuringPoint object) {
				return object.getEntryLevelSystemCall();
			}

			@Override
			public EObject caseExternalCallActionMeasuringPoint(final ExternalCallActionMeasuringPoint object) {
				return object.getExternalCall();
			}

			@Override
			public EObject caseResourceEnvironmentMeasuringPoint(final ResourceEnvironmentMeasuringPoint object) {
				return object.getResourceEnvironment();
			}

			@Override
			public EObject caseSystemOperationMeasuringPoint(final SystemOperationMeasuringPoint object) {
				return object.getOperationSignature();
			}

			@Override
			public EObject caseUsageScenarioMeasuringPoint(final UsageScenarioMeasuringPoint object) {
				return object.getUsageScenario();
			}

		}.doSwitch(measuringPoint);
	}

	public static List<Monitor> getActiveMonitorsForElement(final MonitorRepository monitorRepository,
			final EObject element) {
		final List<Monitor> result = new LinkedList<>();

		if (monitorRepository == null) {
			return result;
		}

		return monitorRepository.getMonitors().stream()
				.filter(m -> m.isActivated()
						&& elementConformingToMeasuringPoint(element, m.getMeasuringPoint()))
				.collect(Collectors.toList());
	}

	public static boolean elementConformingToMeasuringPoint(final EObject element,
			final MeasuringPoint measuringPoint) {
		if (measuringPoint == null) {
			throw new IllegalArgumentException("Measuring point cannot be null");
		}

		Boolean result = checkGeneralMeasuringPoints(element, measuringPoint);

		if (result == null) {
			result = checkPCMMeasuringPoints(element, measuringPoint);

			if (result == null) {
				return true;
			}
		}

		return result.booleanValue();
	}

	private static Boolean checkPCMMeasuringPoints(final EObject element, final MeasuringPoint measuringPoint) {
		return new PcmmeasuringpointSwitch<Boolean>() {

			@Override
			public Boolean caseActiveResourceMeasuringPoint(final ActiveResourceMeasuringPoint object) {
				return this.checkActiveResourceMeasuringPoint(object);
			}

			@Override
			public Boolean caseAssemblyOperationMeasuringPoint(final AssemblyOperationMeasuringPoint object) {
				return this.checkAssemblyOperationMeasuringPoint(object);
			}

			@Override
			public Boolean caseAssemblyPassiveResourceMeasuringPoint(
					final AssemblyPassiveResourceMeasuringPoint object) {
				return this.checkAssemblyPassiveResourceMeasuringPoint(object);
			}

			@Override
			public Boolean caseEntryLevelSystemCallMeasuringPoint(final EntryLevelSystemCallMeasuringPoint object) {
				return this.checkEntryLevelSystemCallMeasuringPoint(element, object);
			}

			@Override
			public Boolean caseExternalCallActionMeasuringPoint(final ExternalCallActionMeasuringPoint object) {
				return this.checkExternCallActionMeasuringpoint(element, object);
			}

			@Override
			public Boolean caseResourceContainerMeasuringPoint(final ResourceContainerMeasuringPoint object) {
				return this.checkResourceContainerMeasuringPoint(element, object);
			}

			@Override
			public Boolean caseResourceEnvironmentMeasuringPoint(final ResourceEnvironmentMeasuringPoint object) {
				return this.checkResourceEnvironmentMeasuringPoint(element, object);
			}

			@Override
			public Boolean caseSystemOperationMeasuringPoint(final SystemOperationMeasuringPoint object) {
				return this.checkSystemOperationMeasuringPoint(object);
			}

			@Override
			public Boolean caseUsageScenarioMeasuringPoint(final UsageScenarioMeasuringPoint object) {
				return this.checkUsageScenarioMeasuringPoint(object);
			}

			private boolean checkActiveResourceMeasuringPoint(final ActiveResourceMeasuringPoint mp) {
				final ProcessingResourceSpecification activeResource = mp.getActiveResource();

				return new ResourceenvironmentSwitch<Boolean>() {

					@Override
					public Boolean caseProcessingResourceSpecification(final ProcessingResourceSpecification object) {
						return activeResource.getId().equals(object.getId());
					}

					@Override
					public Boolean caseResourceContainer(final ResourceContainer object) {
						return object.getId()
								.equals(activeResource.getResourceContainer_ProcessingResourceSpecification().getId());
					}

					@Override
					public Boolean defaultCase(final EObject object) {
						return false;
					}

				}.doSwitch(mp);
			}

			private boolean checkAssemblyOperationMeasuringPoint(final AssemblyOperationMeasuringPoint mp) {
				return new SeffSwitch<Boolean>() {

					@Override
					public Boolean caseExternalCallAction(final ExternalCallAction externalCallAction) {
						return externalCallAction.getCalledService_ExternalService().getId()
								.equals(mp.getOperationSignature().getId())
								&& externalCallAction.getRole_ExternalService().getId().equals(mp.getRole().getId());
					}

					@Override
					public Boolean defaultCase(final EObject object) {
						return false;
					}

				}.doSwitch(element);
			}

			private Boolean checkAssemblyPassiveResourceMeasuringPoint(final AssemblyPassiveResourceMeasuringPoint mp) {

				return new RepositorySwitch<Boolean>() {

					@Override
					public Boolean casePassiveResource(final PassiveResource passiveResource) {
						return passiveResource.getId().equals(mp.getPassiveResource().getId());
					};

					@Override
					public Boolean defaultCase(final EObject object) {
						return false;
					};

				}.doSwitch(element);
			}

			private boolean checkSystemOperationMeasuringPoint(final SystemOperationMeasuringPoint mp) {
				return new UsagemodelSwitch<Boolean>() {

					@Override
					public Boolean caseEntryLevelSystemCall(final EntryLevelSystemCall entryLevelSystemCall) {
						return entryLevelSystemCall.getOperationSignature__EntryLevelSystemCall().getId()
								.equals(mp.getOperationSignature().getId())
								&& entryLevelSystemCall.getProvidedRole_EntryLevelSystemCall().getId()
										.equals(mp.getRole().getId());
					}

					@Override
					public Boolean defaultCase(final EObject object) {
						return false;
					}
				}.doSwitch(element);
			}

			private boolean checkUsageScenarioMeasuringPoint(final UsageScenarioMeasuringPoint mp) {
				return new UsagemodelSwitch<Boolean>() {

					@Override
					public Boolean caseUsageScenario(final UsageScenario usageScenario) {
						return usageScenario.getId().equals(mp.getUsageScenario().getId());
					}

					@Override
					public Boolean defaultCase(final EObject object) {
						return false;
					}
				}.doSwitch(element);
			}

			private Boolean checkResourceEnvironmentMeasuringPoint(final EObject element,
					final ResourceEnvironmentMeasuringPoint mp) {
				return new ResourceenvironmentSwitch<Boolean>() {

					@Override
					public Boolean caseResourceEnvironment(final ResourceEnvironment resourceEnvironment) {
						return resourceEnvironment.getEntityName().equals(mp.getResourceEnvironment().getEntityName());
					};

					@Override
					public Boolean defaultCase(final EObject object) {
						return false;
					}
				}.doSwitch(element);
			};

			private Boolean checkResourceContainerMeasuringPoint(final EObject element,
					final ResourceContainerMeasuringPoint mp) {
				return new ResourceenvironmentSwitch<Boolean>() {

					@Override
					public Boolean caseResourceContainer(final ResourceContainer resourceContainer) {
						return resourceContainer.getId().equals(mp.getResourceContainer().getId());
					};

					@Override
					public Boolean defaultCase(final EObject object) {
						return false;
					}
				}.doSwitch(element);
			}

			private Boolean checkExternCallActionMeasuringpoint(final EObject element,
					final ExternalCallActionMeasuringPoint mp) {
				return new SeffSwitch<Boolean>() {

					@Override
					public Boolean caseExternalCallAction(final ExternalCallAction externalCallAction) {
						return externalCallAction.getId().equals(mp.getExternalCall().getId());
					};

					@Override
					public Boolean defaultCase(final EObject object) {
						return false;
					};
				}.doSwitch(element);
			}

			private Boolean checkEntryLevelSystemCallMeasuringPoint(final EObject element,
					final EntryLevelSystemCallMeasuringPoint mp) {
				return new UsagemodelSwitch<Boolean>() {

					@Override
					public Boolean caseEntryLevelSystemCall(final EntryLevelSystemCall entryLevelSystemCall) {
						return entryLevelSystemCall.getId().equals(mp.getEntryLevelSystemCall().getId());
					};

					@Override
					public Boolean defaultCase(final EObject object) {
						return false;
					};

				}.doSwitch(element);
			}

		}.doSwitch(measuringPoint);
	}

	private static Boolean checkGeneralMeasuringPoints(final EObject element, final MeasuringPoint measuringPoint) {
		return new MeasuringpointSwitch<Boolean>() {

			@Override
			public Boolean caseResourceURIMeasuringPoint(final ResourceURIMeasuringPoint object) {
				final String measuringPointResourceURI = object.getResourceURI();
				final String elementResourceFragment = EMFLoadHelper.getResourceFragment(element);
				return measuringPointResourceURI.endsWith(elementResourceFragment);
			}

			@Override
			public Boolean caseStringMeasuringPoint(final StringMeasuringPoint object) {
				throw new IllegalArgumentException(
						"String measuring points are forbidden for SimuLizar: " + object.toString());
			}

		}.doSwitch(measuringPoint);
	}
}
