package org.palladiosimulator.analyzer.slingshot.annotationprocessor.util;

import java.lang.annotation.Annotation;

import javax.lang.model.element.Element;

import org.palladiosimulator.analyzer.slingshot.annotationprocessor.util.exceptions.IllegalAnnotationArgumentException;

public class AnnotationPreconditions {

	public static void checkAnnotationStringIsNotEmpty(final String stringToCheck, final Element annotatedElement,
	        final Annotation annotation, final String msgOnError) throws IllegalAnnotationArgumentException {
		if (stringToCheck == null || stringToCheck.isEmpty()) {
			throw new IllegalAnnotationArgumentException(annotatedElement, annotation, msgOnError);
		}
	}

	public static void checkAnnotationStringIsNotEmpty(final String stringToCheck, final Element annotatedElement,
	        final Annotation annotation) throws IllegalAnnotationArgumentException {
		checkAnnotationStringIsNotEmpty(stringToCheck, annotatedElement, annotation,
		        "The annotation does not contain correct values.");
	}

}
