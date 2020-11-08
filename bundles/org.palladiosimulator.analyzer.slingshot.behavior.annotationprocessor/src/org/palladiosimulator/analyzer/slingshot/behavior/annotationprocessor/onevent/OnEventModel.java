package org.palladiosimulator.analyzer.slingshot.behavior.annotationprocessor.onevent;

import java.util.List;
import java.util.Map;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.EventCardinality;

public class OnEventModel {

	private final TypeElement annotatedClassElement;
	private final AnnotationMirror annotation;

	private TypeElement whenElement;
	private List<TypeElement> thenElements;
	private EventCardinality cardinality;

	public OnEventModel(final TypeElement annotatedClassElement, final AnnotationMirror annotation) {
		this.annotatedClassElement = annotatedClassElement;
		this.annotation = annotation;

		final Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues = annotation.getElementValues();
		final OnEventValueVisitor valueVisitor = new OnEventValueVisitor();

		for (final Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : elementValues.entrySet()) {
			entry.getValue().accept(valueVisitor, this);
		}
	}

	public TypeElement getWhenElement() {
		return whenElement;
	}

	public void setWhenElement(final TypeElement whenElement) {
		this.whenElement = whenElement;
	}

	public List<TypeElement> getThenElements() {
		return thenElements;
	}

	public void setThenElements(final List<TypeElement> thenElements) {
		this.thenElements = thenElements;
	}

	public EventCardinality getCardinality() {
		return cardinality;
	}

	public void setCardinality(final EventCardinality cardinality) {
		this.cardinality = cardinality;
	}

	public TypeElement getAnnotatedClassElement() {
		return annotatedClassElement;
	}

	public AnnotationMirror getAnnotation() {
		return annotation;
	}
}
