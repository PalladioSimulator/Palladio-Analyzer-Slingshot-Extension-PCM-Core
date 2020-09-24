package org.palladiosimulator.analyzer.slingshot.extensionpoint.annotationprocessor.extensionannotation;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;

import org.palladiosimulator.analyzer.slingshot.annotations.Extension;
import org.palladiosimulator.analyzer.slingshot.annotations.ExtensionAnnotation;
import org.palladiosimulator.analyzer.slingshot.extensionpoint.annotationprocessor.extension.ExtensionModel;

public class ExtensionAnnotationModel {

	private final ExtensionModel extensionModel;
	private final String mustBeBasedOn;

	public ExtensionAnnotationModel(final TypeElement annotationElement, final TypeElement annotatedElement) {
		final ExtensionAnnotation annotation = annotationElement.getAnnotation(ExtensionAnnotation.class);
		final Extension extension = annotation.extensionPoint();

		extensionModel = new ExtensionModel(annotatedElement, extension);

		String mustBeBasedOn;

		try {
			final Class<?> clazz = annotation.mustBaseOn();
			mustBeBasedOn = clazz.getCanonicalName();
		} catch (final MirroredTypeException mte) {
			final DeclaredType classType = (DeclaredType) mte.getTypeMirror();
			final TypeElement element = (TypeElement) classType.asElement();
			mustBeBasedOn = element.getQualifiedName().toString();
		}

		this.mustBeBasedOn = mustBeBasedOn;
	}

	public ExtensionModel getExtensionModel() {
		return extensionModel;
	}

	public String getMustBeBasedOn() {
		return mustBeBasedOn;
	}

}
