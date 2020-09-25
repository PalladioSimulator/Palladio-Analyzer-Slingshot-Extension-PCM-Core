package org.palladiosimulator.analyzer.slingshot.behavior.annotationprocessor;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic.Kind;

import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.EventMethod;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.OnEvent;

public class OnEventProcessor extends AbstractProcessor {

	private Messager messager;

	@Override
	public Set<String> getSupportedAnnotationTypes() {
		return Set.of(OnEvent.class.getCanonicalName(), EventMethod.class.getCanonicalName());
	}

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.RELEASE_8;
	}

	@Override
	public synchronized void init(final ProcessingEnvironment processingEnv) {
		super.init(processingEnv);

		this.messager = processingEnv.getMessager();
	}

	@Override
	public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {

		for (final Element element : roundEnv.getElementsAnnotatedWith(OnEvent.class)) {
			final TypeElement annotatedClassElement = (TypeElement) element;

			for (final ExecutableElement method : ElementFilter
					.methodsIn(annotatedClassElement.getEnclosedElements())) {
				if (method.getAnnotation(EventMethod.class) != null
						|| method.getSimpleName().toString().startsWith("on")) {

					if (checkMethod(method)) {
						messager.printMessage(Kind.ERROR,
								"The event handler must have a ResultEvent return type and a single parameter", method);
					}

				}
			}
		}

		return false;
	}

	private boolean checkMethod(final ExecutableElement method) {
		final TypeMirror returnMirror = method.getReturnType();

		if (returnMirror.getKind().isPrimitive()) {
			return false;
		}

		final DeclaredType returnType = (DeclaredType) returnMirror;
		final TypeElement returnClass = (TypeElement) returnType;

		return returnClass.getQualifiedName().toString().equals("ResultEvent") && method.getParameters().size() == 1;
	}

}
