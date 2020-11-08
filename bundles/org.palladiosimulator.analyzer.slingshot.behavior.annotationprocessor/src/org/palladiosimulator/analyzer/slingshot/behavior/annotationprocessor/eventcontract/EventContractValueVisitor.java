package org.palladiosimulator.analyzer.slingshot.behavior.annotationprocessor.eventcontract;

import java.util.List;

import javax.lang.model.element.AnnotationValue;
import javax.lang.model.util.SimpleAnnotationValueVisitor8;

public class EventContractValueVisitor extends SimpleAnnotationValueVisitor8<Void, String> {

	private final EventContractModel model;

	public EventContractValueVisitor(final EventContractModel model) {
		this.model = model;
	}

	@Override
	public Void visitInt(final int i, final String p) {

		if ("maximumPublishing".equals(p)) {
			model.setMaximumPublishing(i);
		}

		return super.visitInt(i, p);
	}

	@Override
	public Void visitArray(final List<? extends AnnotationValue> vals, final String p) {
		// TODO Auto-generated method stub
		return super.visitArray(vals, p);
	}

}
