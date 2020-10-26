package org.palladiosimulator.analyzer.slingshot.behavior.annotationprocessor;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.TypeElement;

import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.OnEvent;

public class OnEventsModel {

	private final List<OnEventModel> models = new ArrayList<>();

	public OnEventsModel(final TypeElement annotatedClassElement) {
		for (final AnnotationMirror annotationMirror : annotatedClassElement.getAnnotationMirrors()) {
			if (annotationMirror.getAnnotationType().asElement().getSimpleName().toString()
			        .equals(OnEvent.class.getSimpleName())) {
				models.add(new OnEventModel(annotatedClassElement, annotationMirror));
			}
		}
	}

	public List<OnEventModel> getModels() {
		return models;
	}

}
