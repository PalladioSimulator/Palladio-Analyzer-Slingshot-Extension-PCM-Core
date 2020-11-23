package org.palladiosimulator.analyzer.slingshot.behavior.annotationprocessor.onevent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.OnEvent;

public class OnEventsModel {

	private static final Logger LOGGER = Logger.getLogger(OnEventsModel.class);
	private final List<OnEventModel> models = new ArrayList<>();

	@SuppressWarnings("unchecked")
	public OnEventsModel(final TypeElement annotatedClassElement) {
		LOGGER.debug("Initializing OnEventsModel for " + annotatedClassElement.getQualifiedName().toString());
//		for (final AnnotationMirror annotationMirror : annotatedClassElement.getAnnotationMirrors()) {
//			if (annotationMirror.getAnnotationType().asElement().getSimpleName().toString()
//			        .equals(OnEvent.class.getSimpleName())) {
//				LOGGER.debug("Found!");
//				models.add(new OnEventModel(annotatedClassElement, annotationMirror));
//			}
//		}
		final AnnotationMirror onEventsAnnotationMirror = annotatedClassElement.getAnnotationMirrors().stream()
		        .filter(mirror -> mirror.getAnnotationType().asElement().getSimpleName().toString()
		                .equals(OnEvent.OnEvents.class.getSimpleName()))
		        .findFirst()
		        .orElseThrow();

		for (final Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : onEventsAnnotationMirror
		        .getElementValues().entrySet()) {
			/* There should be only one value actually, as this is a repeatable annotation. */
			final List<? extends AnnotationValue> arrayOfOnEvent = (List<? extends AnnotationValue>) entry.getValue()
			        .getValue();
			arrayOfOnEvent.forEach(value -> {
				LOGGER.debug("Found OnEvent");
				final AnnotationMirror mirror = (AnnotationMirror) value.getValue();
				models.add(new OnEventModel(annotatedClassElement, mirror));
			});
		}
	}

	public List<OnEventModel> getModels() {
		return models;
	}

}
