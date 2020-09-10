package org.palladiosimulator.analyzer.slingshot.annotationprocessor;

import java.util.NoSuchElementException;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic.Kind;

import org.apache.log4j.Logger;

@SupportedAnnotationTypes({
	"org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.behavioural.annotations.OnEvent",
	"org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.behavioural.annotations.ProvideEvents"
})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class ContractProcessor extends AbstractProcessor {
	
	private static final String RESULT_EVENT_NAME = "org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.behavioural.results.ResultEvent";
	private static final String EMPTYRESULT_EVENT_NAME = "org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.behavioural.results.EmptyResultEvent";
	
	private static final Logger LOGGER = Logger.getLogger(ContractProcessor.class);
	
	private Messager messager;
	private DeclaredType resultEventType;
	private DeclaredType emptyResultEventType;
	
	
	@Override
	public synchronized void init(final ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		
		this.messager = processingEnv.getMessager();
		
		final TypeElement resultEventElement = processingEnv.getElementUtils().getTypeElement(RESULT_EVENT_NAME);
		final TypeElement emptyResultEventElement = processingEnv.getElementUtils().getTypeElement(EMPTYRESULT_EVENT_NAME);
		
		this.resultEventType = processingEnv.getTypeUtils().getDeclaredType(resultEventElement);
		this.emptyResultEventType = processingEnv.getTypeUtils().getDeclaredType(emptyResultEventElement);
	}



	@Override
	public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
		
		for (final TypeElement annotation : annotations) {
			
			for (final Element element : roundEnv.getElementsAnnotatedWith(annotation)) {
				
				if (annotation.getSimpleName().toString().equals("OnEvent") && element.getKind() == ElementKind.CLASS) {
					LOGGER.info("OnEvent was found");
					
					final TypeElement classElement = (TypeElement) element;
					final AnnotationMirror onEventAnnotation = getAnnotation(annotation, (DeclaredType) annotation.asType());
					
					checkContract(classElement, onEventAnnotation);
					
				} else if (annotation.getSimpleName().toString().equals("ProvideEvents") && element.getKind() == ElementKind.METHOD) {
					LOGGER.info("ProvideEvents was found");
				}
				
			}
			
		}
		
		return true;
	}
	
	
	private void checkContract(final TypeElement classElement, final AnnotationMirror onEventAnnotation) {
		classElement.getEnclosedElements().stream()
					.filter(element -> element.getKind() == ElementKind.METHOD)
					.map(element -> (ExecutableElement) element)
					.filter(element -> element.getSimpleName().toString().startsWith("on")) // FIXME: Or annotated with @Event
					.filter(element -> methodFitsToAnnotation(element, onEventAnnotation))
					.forEach(method -> {
						checkContractOfSingleMethod(method, onEventAnnotation);
					});
	}

	private boolean methodFitsToAnnotation(final ExecutableElement element, final AnnotationMirror onEventAnnotation) {
		final AnnotationValue whenValue = getWhenValue(onEventAnnotation);
		final String clazzName = ((Class<?>) whenValue.getValue()).getName();
		
		if (element.getParameters().size() == 1) {
			final TypeMirror type = element.getParameters().get(0).asType();
			
			final TypeElement clazz = processingEnv.getElementUtils().getTypeElement(clazzName);
			final DeclaredType clazzType = processingEnv.getTypeUtils().getDeclaredType(clazz);
			
			if (type.getKind() == TypeKind.DECLARED 
					&& processingEnv.getTypeUtils().isSameType(type, clazzType)) {
				return true;
			}
		}
		
		return false;
	}

	private void checkContractOfSingleMethod(final ExecutableElement method, final AnnotationMirror onEventAnnotation) {
		final TypeMirror returnType = method.getReturnType();
		
		if (returnType.getKind() != TypeKind.DECLARED || this.isCastableResultEvent((DeclaredType) returnType)) {
			messager.printMessage(Kind.ERROR, 
					"An event handler method MUST return a value of type ResultEvent (or a sub-type of it).",
					method);
		}
		
		/* Here, the method returns a ResultEvent */
		final AnnotationValue thenValue = getThenValue(onEventAnnotation);
		final Class<?>[] desEventClazzes = (Class<?>[]) thenValue.getValue(); // FIXME: One cannot reference to the actual DESEvent, as that class might not be loaded yet.
		
		if (desEventClazzes.length == 0) {
			if (processingEnv.getTypeUtils().isSameType(returnType, this.emptyResultEventType)) {
				messager.printMessage(Kind.ERROR, 
						"The contract does not specify any subsequent events for this event handler. Either set the 'then' value correctly of OnEvent, or use EmptyResultEvent as the return type",
						method);
			}
		}
		
		// TODO: Check when then value is set.
	}
	
	private boolean isCastableResultEvent(final DeclaredType returnType) {
		return processingEnv.getTypeUtils().isAssignable(returnType, this.resultEventType);
	}
	
	private AnnotationValue getWhenValue(final AnnotationMirror onEventAnnotation) {
		return this.getAnnotationValue(onEventAnnotation, this.getMethod(onEventAnnotation.getAnnotationType().asElement(), "when"));
	}
	
	private AnnotationValue getThenValue(final AnnotationMirror onEventAnnotation) {
		return this.getAnnotationValue(onEventAnnotation, this.getMethod(onEventAnnotation.getAnnotationType().asElement(), "then"));
	}
	
	private AnnotationValue getAnnotationValue(final AnnotationMirror annotation, final ExecutableElement key) {
		return annotation.getElementValues().get(key);
	}
	
	/**
	 * Helper method returning the executable element (a method) that is enclosed by the element and has
	 * the name methodName. If no such element is found, a NoSuchElementException is thrown. This is used
	 * in order to get the fields from the annotations.
	 * 
	 * @param element the enclosing element that contains the method to be found. Must not be null.
	 * @param methodName the name of that method to be found. Must not be null or empty.
	 * @return the executable element if it is found.
	 * @throws NoSuchElementException if it is not found.
	 */
	private ExecutableElement getMethod(final Element element, final String methodName) {
		assert(element != null);
		assert(methodName != null && !methodName.isEmpty());
		
		for (final ExecutableElement exElement : ElementFilter.methodsIn(element.getEnclosedElements())) {
			if (exElement.getSimpleName().toString().equals(methodName)) {
				return exElement;
			}
		}
		
		// No element was found.
		throw new NoSuchElementException("The method of name " + methodName + " cannot be found.");
	}
	
	private AnnotationMirror getAnnotation(final Element element, final DeclaredType annotationType) {
		for (final AnnotationMirror mirror : element.getAnnotationMirrors()) {
			if (processingEnv.getTypeUtils().isSameType(mirror.getAnnotationType(), annotationType)) {
				return mirror;
			}
		}
		
		throw new NoSuchElementException("Annotation of type " + annotationType.toString() + " could not be found.");
	}
}
