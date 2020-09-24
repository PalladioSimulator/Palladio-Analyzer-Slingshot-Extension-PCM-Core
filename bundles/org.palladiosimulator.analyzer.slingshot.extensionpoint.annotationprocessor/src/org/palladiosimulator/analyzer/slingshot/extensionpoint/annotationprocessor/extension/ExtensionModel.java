package org.palladiosimulator.analyzer.slingshot.extensionpoint.annotationprocessor.extension;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;

import org.palladiosimulator.analyzer.slingshot.annotations.Extension;

/**
 * This represents the data model for the Extension annotation.
 * 
 * @author Julijan Katic
 */
public class ExtensionModel {

	private final TypeElement annotatedClassElement;
	private final String extensionPointId;
	private final String extensionPointImplementationNode;
	private final String implementation;

	public ExtensionModel(final TypeElement annotatedElement, final Extension annotation) {
		this.annotatedClassElement = annotatedElement;
		extensionPointId = annotation.extensionPointId();
		extensionPointImplementationNode = annotation.extensionPointImplementationNode();

		if (extensionPointId.isEmpty() || extensionPointImplementationNode.isEmpty()) {
			throw new IllegalArgumentException(
					"Both extensionPointId and extensionPointImplementationNode must not be empty nor null.");
		}

		String implementationName;

		try {
			final Class<?> clazz = annotation.implementation();
			implementationName = clazz.getCanonicalName();
		} catch (final MirroredTypeException mte) {
			/*
			 * If the class could not be / is not loaded yet, then this exception is thrown
			 * and its TypeElement is stored in mte.
			 */
			final DeclaredType classTypeMirror = (DeclaredType) mte.getTypeMirror();
			final TypeElement classTypeElement = (TypeElement) classTypeMirror.asElement();
			implementationName = classTypeElement.getQualifiedName().toString();
		}

		if (implementationName.equals(Object.class.getCanonicalName())) {
			/* The standard value was used -> use the class annotated on */
			implementationName = getAnnotatedClassElement().getQualifiedName().toString();
		}

		this.implementation = implementationName;
	}

	public ExtensionModel(final TypeElement element) throws IllegalArgumentException {
		this(element, element.getAnnotation(Extension.class));
	}

	public String getExtensionPointId() {
		return extensionPointId;
	}

	public String getExtensionPointImplementationNode() {
		return extensionPointImplementationNode;
	}

	public String getImplementation() {
		return implementation;
	}

	public TypeElement getAnnotatedClassElement() {
		return annotatedClassElement;
	}

}
