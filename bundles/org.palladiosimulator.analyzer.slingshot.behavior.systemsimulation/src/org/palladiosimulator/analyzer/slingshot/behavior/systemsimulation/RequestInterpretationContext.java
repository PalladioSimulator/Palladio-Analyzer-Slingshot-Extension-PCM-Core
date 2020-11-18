package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation;

import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.Request;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.UserInterpretationContext;
import org.palladiosimulator.pcm.seff.AbstractAction;

public class RequestInterpretationContext {

	private UserInterpretationContext userInterpretationContext;
	private RequestInterpretationContext parent;
	private Request request;
	private AbstractAction seffAction;

	public RequestInterpretationContext(final UserInterpretationContext userInterpretationContext,
	        final RequestInterpretationContext parent) {
		this.userInterpretationContext = userInterpretationContext;
		this.parent = parent;
	}

	public RequestInterpretationContext() {
	}

	public UserInterpretationContext getUserInterpretationContext() {
		return userInterpretationContext;
	}

	public void setUserInterpretationContext(final UserInterpretationContext userInterpretationContext) {
		this.userInterpretationContext = userInterpretationContext;
	}

	public RequestInterpretationContext getParent() {
		return parent;
	}

	public void setParent(final RequestInterpretationContext parent) {
		this.parent = parent;
	}

	public Request getRequest() {
		return request;
	}

	public void setRequest(final Request request) {
		this.request = request;
	}

	public AbstractAction getSeffAction() {
		return seffAction;
	}

	public void setSeffAction(final AbstractAction seffAction) {
		this.seffAction = seffAction;
	}

}
