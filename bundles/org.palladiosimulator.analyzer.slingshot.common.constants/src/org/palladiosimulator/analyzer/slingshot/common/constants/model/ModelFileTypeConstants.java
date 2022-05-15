package org.palladiosimulator.analyzer.slingshot.common.constants.model;

public class ModelFileTypeConstants {
	public static final String EMPTY_STRING = "";

	public static final String ALLOCATION_FILE = "allocationmodel";
	public static final String USAGE_FILE = "usagemodel";
	public static final String MONITOR_REPOSITORY_FILE = "monitorRepositoryFile";
	public static final String SCALING_POLICY_DEFINITION_FILE = "scalingPolicyDefinitionFile";
	public static final String LOG_FILE = "logFile";

	public static final String[] ALLOCATION_FILE_EXTENSION = new String[] {
			"*." + ModelTypeConstants.ALLOCATION_EXTENSION };
	public static final String[] USAGEMODEL_FILE_EXTENSION = new String[] {
			"*." + ModelTypeConstants.USAGEMODEL_EXTENSION };
	public static final String[] MONITOR_REPOSITORY_FILE_EXTENSION = new String[] {
			"*." + ModelTypeConstants.MONITOR_REPOSITORY_EXTENSION };
	public static final String[] SCALING_POLICY_DEFINITION_FILE_EXTENSION = new String[] {
			"*." + ModelTypeConstants.SCALING_POLICY_DEFINITION_EXTENSION };
	public static final String[] LOG_FILE_EXTENSIONS = new String[] { "*.txt", "*.log" };
}
