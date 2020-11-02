package org.palladiosimulator.analyzer.slingshot.behavior.annotationprocessor;

import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import org.palladiosimulator.analyzer.slingshot.annotationprocessor.util.exceptions.IllegalAnnotationArgumentException;
import org.palladiosimulator.analyzer.slingshot.annotationprocessor.util.exceptions.ProcessException;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.EventContract;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.OnEvent;

/**
 * This processor is responsible for the {@link EventContract} annotations. This
 * additionally validates the {@link OnEvent} contracts in such a way that the
 * {@link EventContract}s are not violated by the {@link OnEvent} contracts.
 * 
 * @author Julijan Katic
 */
public class EventContractProcessor extends AbstractProcessor {

	private Messager messager;
	private Types typeUtils;
	private Elements elementUtils;

	@Override
	public synchronized void init(final ProcessingEnvironment processingEnv) {
		super.init(processingEnv);

		this.messager = processingEnv.getMessager();
		this.typeUtils = processingEnv.getTypeUtils();
		this.elementUtils = processingEnv.getElementUtils();
	}

	@Override
	public Set<String> getSupportedAnnotationTypes() {
		return Set.of(OnEvent.class.getName(), EventContract.class.getName());
	}

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latest();
	}

	@Override
	public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
		try {
			final Set<TypeElement> annotatedEventContractClasses = ElementFilter
			        .typesIn(roundEnv.getElementsAnnotatedWith(EventContract.class));
			checkEventContractOnlyOnEvents(annotatedEventContractClasses);

			final DeclaredType eventContractType = (DeclaredType) elementUtils
			        .getTypeElement(EventContract.class.getCanonicalName()).asType();

			final Set<EventContractModel> models = annotatedEventContractClasses.stream()
			        .map(element -> new EventContractModel(element, eventContractType, typeUtils))
			        .collect(Collectors.toSet());

			models.forEach(model -> {
				try {
					checkIntRange(model);
				} catch (final ProcessException e) {
					e.showError(messager);
				}
			});

		} catch (final ProcessException e) {
			e.showError(messager);
		}

		return false;
	}

	/**
	 * Checks whether the {@link EventContract} are only annotated on classes that
	 * are a subtype of {@link DESEvent}.
	 * 
	 * @param elements The elements annotated with {@link EventContract}
	 * @throws ProcessException If there is an element in {@code elements} which is
	 *                          not a subtype of {@link DESEvent}.
	 */
	private void checkEventContractOnlyOnEvents(final Set<TypeElement> elements) throws ProcessException {
		final DeclaredType desEventType = (DeclaredType) elementUtils.getTypeElement(DESEvent.class.getCanonicalName())
		        .asType();

		for (final TypeElement typeElement : elements) {
			final DeclaredType clazzType = (DeclaredType) typeElement.asType();

			if (!typeUtils.isSubtype(clazzType, desEventType)) {
				throw new ProcessException(typeElement,
				        "Classes with @EventContract must implement " + DESEvent.class.getName());
			}
		}
	}

	private void checkIntRange(final EventContractModel model) throws ProcessException {
		if (model.getMaximumPublishing() < 0) {
			throw new IllegalAnnotationArgumentException(model.getAnnotatedElement(), model.getAnnotation(),
			        "maximalPublishing must be a positive number or 0.");
		}
	}
}
