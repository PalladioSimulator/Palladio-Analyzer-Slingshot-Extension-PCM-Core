package org.palladiosimulator.analyzer.slingshot.common.serialization.load;

import java.io.File;
import java.nio.file.Path;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage.Registry;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.palladiosimulator.analyzer.slingshot.common.constants.model.ModelTypeConstants;
import org.palladiosimulator.pcm.PcmPackage;
import org.palladiosimulator.pcm.allocation.util.AllocationResourceFactoryImpl;
import org.palladiosimulator.pcm.repository.util.RepositoryResourceFactoryImpl;
import org.palladiosimulator.pcm.resourceenvironment.util.ResourceenvironmentResourceFactoryImpl;
import org.palladiosimulator.pcm.resourcetype.util.ResourcetypeResourceFactoryImpl;
import org.palladiosimulator.pcm.system.util.SystemResourceFactoryImpl;
import org.palladiosimulator.pcm.usagemodel.UsagemodelPackage;
import org.palladiosimulator.pcm.usagemodel.util.UsagemodelResourceFactoryImpl;

import de.uka.ipd.sdq.identifier.IdentifierPackage;
import de.uka.ipd.sdq.units.UnitsPackage;

public class EMFResourceSetInitializerHelper {
	
	public static void initEMF(final Path usageModelPath) {
		
		// register default resource factories in the standalone EMF environment
		Resource.Factory.Registry registry = Resource.Factory.Registry.INSTANCE;
		
		registry.getExtensionToFactoryMap().put(ModelTypeConstants.REPOSITORY_EXTENSION, new RepositoryResourceFactoryImpl());
		registry.getExtensionToFactoryMap().put(ModelTypeConstants.RESOURCE_ENVIRONMENT_EXTENSION, new ResourceenvironmentResourceFactoryImpl());
		registry.getExtensionToFactoryMap().put(ModelTypeConstants.SYSTEM_EXTENSION,	new SystemResourceFactoryImpl());
		registry.getExtensionToFactoryMap().put(ModelTypeConstants.ALLOCATION_EXTENSION,	new AllocationResourceFactoryImpl());
		registry.getExtensionToFactoryMap().put(ModelTypeConstants.RESOURCETYPE_EXTENSION, new ResourcetypeResourceFactoryImpl());
		registry.getExtensionToFactoryMap().put(ModelTypeConstants.USAGEMODEL_EXTENSION, new UsagemodelResourceFactoryImpl());
		
		// register your package(s)
//		Registry.INSTANCE.put(AllocationPackage.eNS_URI, AllocationPackage.eINSTANCE);
//		Registry.INSTANCE.put(CorePackage.eNS_URI, CorePackage.eINSTANCE);
//		Registry.INSTANCE.put(CompletionsPackage.eNS_URI, CompletionsPackage.eINSTANCE);
//		Registry.INSTANCE.put(ParameterPackage.eNS_URI, ParameterPackage.eINSTANCE);
//		Registry.INSTANCE.put(SeffPackage.eNS_URI, SeffPackage.eINSTANCE);
//		Registry.INSTANCE.put(StoexPackage.eNS_URI, StoexPackage.eINSTANCE);
//		Registry.INSTANCE.put(SystemPackage.eNS_URI, SystemPackage.eINSTANCE);
//		Registry.INSTANCE.put(RepositoryPackage.eNS_URI, RepositoryPackage.eINSTANCE);
//		Registry.INSTANCE.put(ResourcetypePackage.eNS_URI, ResourcetypePackage.eINSTANCE);
//		Registry.INSTANCE.put(ResourceenvironmentPackage.eNS_URI, ResourceenvironmentPackage.eINSTANCE);
		Registry.INSTANCE.put(UsagemodelPackage.eNS_URI, UsagemodelPackage.eINSTANCE);

		Registry.INSTANCE.put(EcorePackage.eNS_URI,  EcorePackage.eINSTANCE);
		Registry.INSTANCE.put(IdentifierPackage.eNS_URI, IdentifierPackage.eINSTANCE);
		Registry.INSTANCE.put(UnitsPackage.eNS_URI, UnitsPackage.eINSTANCE);
		Registry.INSTANCE.put(PcmPackage.eNS_URI, PcmPackage.eINSTANCE);
		
//       ,
//        ProbfunctionPackage.eINSTANCE,
//        ,
//        , ReliabilityPackage.eINSTANCE
//        , QosReliabilityPackage.eINSTANCE
//        , SeffReliabilityPackage.eINSTANCE
		
		
		/**
		 * A remapping of the URI pathmap://... must be done in the plugin.xml of this plugin
		 *     public static final URI PCM_PALLADIO_RESOURCE_TYPE_URI = URI.createURI("pathmap://PCM_MODELS/Palladio.resourcetype");
		 *     public static final URI PCM_PALLADIO_PRIMITIVE_TYPE_REPOSITORY_URI = URI.createURI("pathmap://PCM_MODELS/PrimitiveTypes.repository");
		 * 
		 * otherwise the resource loading in PreparePCMBlackboardPartitionJob l. 45/46 is not working
		 * 
		 * Configured mapping: (ref. https://www-01.ibm.com/support/docview.wss?uid=swg21281749)
		 *  - register extension ' org.eclipse.emf.ecore.uri_mapping'
		 *  - add remapping: pathmap://PCM_MODELS/PrimitiveTypes.repository to platform:/plugin/org.palladiosimulator.pcm.resources/defaultModels/PrimitiveTypes.repository
		 *  - add remapping: pathmap://PCM_MODELS/Palladio.resourcetype to platform:/plugin/org.palladiosimulator.pcm.resources/defaultModels/Palladio.resourcetype
		 * */
		final Map<URI, URI> uriMap = URIConverter.URI_MAP;
		uriMap.put(URI.createURI(ModelTypeConstants.PALLADIO_RESOURCETYPES_PATHMAP), URI.createURI(ModelTypeConstants.PALLADIO_RESOURCETYPES_PATHMAP_TARGET));
		uriMap.put(URI.createURI(ModelTypeConstants.PRIMITIVE_TYPES_REPO_PATHMAP), URI.createURI(ModelTypeConstants.PRIMITIVE_TYPES_REPO_PATHMAP_TARGET));
		
		// map platform URIs to file URIs to run in standalone mode
//		uriMap.put(SimulationWorkflowConfigurationConstants.allocationPlatformUri, SimulationWorkflowConfigurationConstants.allocationFileUri);
//		uriMap.put(ModelTypeConstants.usageModelPlatformUri, ModelTypeConstants.usageModelFileUri);
	}

}