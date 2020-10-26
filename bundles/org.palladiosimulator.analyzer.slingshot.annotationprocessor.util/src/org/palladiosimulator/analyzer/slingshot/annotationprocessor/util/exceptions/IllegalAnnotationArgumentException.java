package org.palladiosimulator.analyzer.slingshot.annotationprocessor.util.exceptions;

import java.lang.annotation.Annotation;

import javax.annotation.processing.Messager;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic.Kind;

public class IllegalAnnotationArgumentException extends ProcessException {

	private final Annotation annotation;
	private final AnnotationMirror annotationMirror;

	@Deprecated
	public IllegalAnnotationArgumentException(final Element element, final Annotation annotation, final String msg) {
		super(element, msg);
		this.annotation = annotation;
		this.annotationMirror = null;
	}

	public IllegalAnnotationArgumentException(final Element element, final AnnotationMirror annotation,
	        final String msg) {
		super(element, msg);
		this.annotation = null;
		this.annotationMirror = annotation;
	}

	@Deprecated
	public Annotation getAnnotation() {
		return annotation;
	}

	@Override
	public void showError(final Messager messager) {
		if (annotationMirror != null) {
			messager.printMessage(Kind.ERROR, getMessage(), getElement(), annotationMirror);
		} else {
			super.showError(messager);
		}
	}

}
