package org.palladiosimulator.analyzer.slingshot.simulation.core.extensions;

public class ContractResult {
	
	private boolean failed;
	private String message;
	
	public ContractResult(boolean failed, String message) {
		super();
		this.failed = failed;
		this.message = message;
	}

	public boolean isFailed() {
		return failed;
	}

	public String getMessage() {
		return message;
	}
	
	
	
	
	
}
