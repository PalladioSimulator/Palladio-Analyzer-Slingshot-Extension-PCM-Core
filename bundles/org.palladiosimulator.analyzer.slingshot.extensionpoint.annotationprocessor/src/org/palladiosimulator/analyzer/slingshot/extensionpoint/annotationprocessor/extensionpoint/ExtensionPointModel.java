package org.palladiosimulator.analyzer.slingshot.extensionpoint.annotationprocessor.extensionpoint;

import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.TypeElement;

import org.palladiosimulator.analyzer.slingshot.annotations.ExtensionPoint;
import org.palladiosimulator.analyzer.slingshot.extensionpoint.annotationprocessor.AnnotationPreconditions;
import org.palladiosimulator.analyzer.slingshot.extensionpoint.annotationprocessor.IllegalAnnotationArgumentException;

import com.google.common.base.Preconditions;

/**
 * A model containing all the information that the
 * {@link ExtensionPoint}-Annotation provides.
 * 
 * @author Julijan Katic
 */
public class ExtensionPointModel {

	private final TypeElement annotatedElement;
	private final String extensionPointId;
	private final String extensionPointName;
	private final String pluginName;
	private final String providerNode;

	/**
	 * Instanciates the extension point model by using the corresponding the
	 * {@link ExtensionPoint} annotation.
	 * 
	 * @param annotatedElement The class onto which the annotation is .
	 * @param extensionPoint   The annotation itself.
	 * @throws IllegalAnnotationArgumentException if a value in extensionPoint is
	 *                                            empty or any of the argument is
	 *                                            null.
	 */
	public ExtensionPointModel(final TypeElement annotatedElement, final ExtensionPoint extensionPoint)
			throws IllegalAnnotationArgumentException {
		Preconditions.checkNotNull(annotatedElement);
		Preconditions.checkNotNull(extensionPoint);
		AnnotationPreconditions.checkNonEmptyString(extensionPoint.id(), extensionPoint, annotatedElement,
				"The extension-point ID must not be empty.");
		AnnotationPreconditions.checkNonEmptyString(extensionPoint.name(), extensionPoint, annotatedElement,
				"The extension-point name must not be empty.");
		AnnotationPreconditions.checkNonEmptyString(extensionPoint.pluginName(), extensionPoint, annotatedElement,
				"The plugin name providing the extension-point must not be empty.");
		AnnotationPreconditions.checkNonEmptyString(extensionPoint.providerNode(), extensionPoint, annotatedElement,
				"The providerNode must not be empty");

		this.annotatedElement = annotatedElement;

		this.extensionPointId = extensionPoint.id();
		this.extensionPointName = extensionPoint.name();
		this.pluginName = extensionPoint.pluginName();
		this.providerNode = extensionPoint.providerNode();

	}

	public ExtensionPointModel(final TypeElement annotatedElement) throws IllegalAnnotationArgumentException {
		this(annotatedElement, annotatedElement.getAnnotation(ExtensionPoint.class));
	}

	public String getAnnotatedClassName() {
		return annotatedElement.getQualifiedName().toString();
	}

	public TypeElement getAnnotatedElement() {
		return annotatedElement;
	}

	public String getExtensionPointId() {
		return extensionPointId;
	}

	public String getExtensionPointName() {
		return extensionPointName;
	}

	public String getPluginName() {
		return pluginName;
	}

	public String getProviderNode() {
		return providerNode;
	}

	/**
	 * Returns a String to String map that contains the entire extension-point model
	 * information including the qualified class name that the extender has to
	 * implement/extend.
	 * 
	 * @return a non-null map containing all the important information described
	 *         above.
	 */
	public Map<String, String> getStringMap() {
		final Map<String, String> hashMap = new HashMap<>();

		hashMap.put("extensionPointId", extensionPointId);
		hashMap.put("extensionPointName", extensionPointName);
		hashMap.put("pluginName", pluginName);
		hashMap.put("providerNode", providerNode);
		hashMap.put("basedOn", this.getAnnotatedClassName());

		return hashMap;
	}
}
