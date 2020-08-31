package org.palladiosimulator.analyzer.slingshot.annotationprocessor;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

@SupportedAnnotationTypes("org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.behavioural.annotations.*")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class ContractProcessor extends AbstractProcessor {
	
	private Messager messager;
	
	
	@Override
	public synchronized void init(final ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		
		this.messager = processingEnv.getMessager();
	}



	@Override
	public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
		
		return false;
	}

}
