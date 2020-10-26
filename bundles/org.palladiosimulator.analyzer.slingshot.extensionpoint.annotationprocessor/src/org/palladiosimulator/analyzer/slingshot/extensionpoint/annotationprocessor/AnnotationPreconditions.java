package org.palladiosimulator.analyzer.slingshot.extensionpoint.annotationprocessor;

import java.lang.annotation.Annotation;

import javax.lang.model.element.Element;

public class AnnotationPreconditions {

	public static void checkNonEmptyString(final String stringToCheck, final Annotation annotation,
			final Element annotatedElement, final String message, final Object... args)
			throws IllegalAnnotationArgumentException {
		if (stringToCheck == null || stringToCheck.isEmpty()) {
			throw new IllegalAnnotationArgumentException(String.format(message, args), annotation, annotatedElement);
		}
	}

	public static void checkNonEmptyString(final String stringToCheck, final Annotation annotation,
			final Element annotatedElement)
			throws IllegalAnnotationArgumentException {
		checkNonEmptyString(stringToCheck, annotation, annotatedElement,
				"The annotation does not contain correct values.");
	}

}
