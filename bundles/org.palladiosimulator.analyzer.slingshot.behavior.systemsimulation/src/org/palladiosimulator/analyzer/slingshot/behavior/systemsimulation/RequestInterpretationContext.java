package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation;

import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.Request;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.UserInterpretationContext;

public class RequestInterpretationContext {

	private final UserInterpretationContext userInterpretationContext;
	public UserInterpretationContext getUserInterpretationContext() {
		return userInterpretationContext;
	}

	private final RequestInterpretationContext parent;
	
	public RequestInterpretationContext() {
		super();
		parent = null;
		userInterpretationContext = null;
	}
	
	public RequestInterpretationContext(final RequestInterpretationContext parent) {
		super();
		this.parent = parent;
		userInterpretationContext = null;
	}
	
	public RequestInterpretationContext(final UserInterpretationContext userInterpretationContext) {
		super();
		this.parent = new RequestInterpretationContext();
		this.userInterpretationContext = userInterpretationContext;
	}

	public Request getRequest() {
		// TODO Auto-generated method stub
		return null;
	}

	public RequestInterpretationContext getParent() {
		return parent;
	}

}
