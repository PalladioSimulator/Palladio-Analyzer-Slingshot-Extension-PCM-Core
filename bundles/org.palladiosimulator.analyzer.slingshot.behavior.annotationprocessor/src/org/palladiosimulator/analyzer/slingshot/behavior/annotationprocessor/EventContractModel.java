package org.palladiosimulator.analyzer.slingshot.behavior.annotationprocessor;

import java.util.Map;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.util.Types;

public class EventContractModel {

	private final TypeElement annotatedElement;
	private final AnnotationMirror annotation;
	private int maximumPublishing;

	public EventContractModel(final TypeElement annotatedClassElement, final DeclaredType annotationType,
	        final Types typeUtils) {
		this.annotatedElement = annotatedClassElement;

		AnnotationMirror foundAnnotation = null;

		for (final AnnotationMirror annotationMirror : annotatedClassElement.getAnnotationMirrors()) {
			if (typeUtils.isSameType(annotationType, annotationMirror.getAnnotationType())) {
				foundAnnotation = annotationMirror;
			}
		}

		this.annotation = foundAnnotation;

		final Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues = annotation.getElementValues();
		final EventContractValueVisitor valueVisitor = new EventContractValueVisitor(this);

		for (final Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : elementValues.entrySet()) {
			entry.getValue().accept(valueVisitor, entry.getKey().getSimpleName().toString());
		}
	}

	public int getMaximumPublishing() {
		return maximumPublishing;
	}

	public void setMaximumPublishing(final int maximumPublishing) {
		this.maximumPublishing = maximumPublishing;
	}

	public TypeElement getAnnotatedElement() {
		return annotatedElement;
	}

	public AnnotationMirror getAnnotation() {
		return annotation;
	}

}
