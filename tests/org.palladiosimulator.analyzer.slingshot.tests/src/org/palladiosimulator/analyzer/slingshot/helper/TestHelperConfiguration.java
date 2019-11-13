package org.palladiosimulator.analyzer.slingshot.helper;

import java.io.File;

import org.eclipse.emf.common.util.URI;

public class TestHelperConfiguration {
	
	public static String BASE_DIR = System.getProperty("user.dir");

	public static final String MODEL_FOLDER = "/org.palladiosimulator.analyzer.performability.domain.tests/testmodel";
	public static final String ALLOCATION_PATH = MODEL_FOLDER + File.separator + "default.allocation";
	public static final String USAGE_MODEL_PATH = MODEL_FOLDER + File.separator + "default.usagemodel";
	public static final String MONITOR_REPOSITORY_MODEL_PATH = MODEL_FOLDER + File.separator + "default.monitorrepository";

	public static final String REPOSITORY_EXTENSION = "repository";
	public static final String RESOURCE_ENVIRONMENT_EXTENSION = "resourceenvironment";
	public static final String SYSTEM_EXTENSION = "system";
	public static final String ALLOCATION_EXTENSION = "allocation";
	public static final String RESOURCETYPE_EXTENSION = "resourcetype";
	public static final String USAGEMODEL_EXTENSION = "usagemodel";

	public static final String PALLADIO_RESOURCETYPES_PATHMAP = "pathmap://PCM_MODELS/Palladio.resourcetype";
	public static final String PALLADIO_RESOURCETYPES_PATHMAP_TARGET = "platform:/plugin/org.palladiosimulator.pcm.resources/defaultModels/Palladio.resourcetype";
	
	public static final String PRIMITIVE_TYPES_REPO_PATHMAP = "pathmap://PCM_MODELS/PrimitiveTypes.repository";
	public static final String PRIMITIVE_TYPES_REPO_PATHMAP_TARGET = "platform:/plugin/org.palladiosimulator.pcm.resources/defaultModels/PrimitiveTypes.repository";


	
	public static URI allocationPlatformUri = URI.createPlatformPluginURI(TestHelperConfiguration.ALLOCATION_PATH, true);
	public static URI usageModelPlatformUri = URI.createPlatformPluginURI(TestHelperConfiguration.USAGE_MODEL_PATH, true);
	
	public static URI allocationFileUri = URI.createFileURI(BASE_DIR + File.separator + "testmodel" + File.separator +  "default.allocation");
	public static URI usageModelFileUri = URI.createFileURI(BASE_DIR + File.separator + "testmodel" + File.separator +  "default.usagemodel");
	public static URI monitorRepositoryModelFileUri = URI.createFileURI(BASE_DIR + File.separator + "testmodel" + File.separator +  "default.monitorrepository");
	
//    public static URI allocationUri;
//    public static URI usageModelUri;
//    private static URI monitorRepoUri;
//    private static URI reconfigurationRulesUri;
//    private static URI usageEvolutionModelUri;
//    private static URI emptyUsageEvolutionModelUri;
//    private static URI sloRepoUri;



}
