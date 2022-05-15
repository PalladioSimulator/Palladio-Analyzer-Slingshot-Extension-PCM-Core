package org.palladiosimulator.analyzer.slingshot.workflow.configuration;

public final class SlingshotSpecificWorkflowConfiguration {
	
	private final String logFileName;
	
	public SlingshotSpecificWorkflowConfiguration(final Builder builder) {
		this.logFileName = builder.logFileName;
	}
	
	public String getLogFileName() {
		return this.logFileName;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static final class Builder {
		private String logFileName;
		
		private Builder() {}
		
		public Builder withLogFile(final String logFileName) {
			this.logFileName = logFileName;
			return this;
		}
		
		public SlingshotSpecificWorkflowConfiguration build() {
			return new SlingshotSpecificWorkflowConfiguration(this);
		}
	}
}
