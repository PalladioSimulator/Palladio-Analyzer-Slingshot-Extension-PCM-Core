package org.palladiosimulator.analyzer.slingshot.annotationprocessor;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

@SupportedAnnotationTypes("org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.behavioural.annotations.ProvidesEvents")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class DESEventSetProcessor extends AbstractProcessor {
	
	private Messager messager;

	@Override
	public synchronized void init(final ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		this.messager = processingEnv.getMessager();
	}

	@Override
	public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
		checkProvidesEventsAnnotatedFields(roundEnv);
		return false;
	}

	private void checkProvidesEventsAnnotatedFields(final RoundEnvironment roundEnv) {
		final Set<? extends Element> annotatedFields = roundEnv.getElementsAnnotatedWith(processingEnv.getElementUtils().getTypeElement("org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.behavioural.annotations.ProvidesEvents"));
		
	}

}
