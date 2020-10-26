package org.palladiosimulator.analyzer.slingshot.extensionpoint.annotationprocessor;

import java.lang.annotation.Annotation;

import javax.lang.model.element.Element;

/**
 * This exception is say that a certain annotation value is not correct, but
 * unlike IllegalArgumentException, this will also contain the annotation
 * itself.
 * 
 * @author Julijan Katic
 */
public class IllegalAnnotationArgumentException extends Exception {

	private final Annotation annotation;
	private final Element annotatedElement;

	public IllegalAnnotationArgumentException(final String message, final Annotation annotation,
			final Element annotatedElement) {
		super(message);
		this.annotation = annotation;
		this.annotatedElement = annotatedElement;
	}

	public IllegalAnnotationArgumentException(final Annotation annotation) {
		this("This annotation contain incorrect values.", annotation, null);
	}

	public Annotation getAnnotation() {
		return annotation;
	}

	public Element getAnnotatedElement() {
		return annotatedElement;
	}

}
