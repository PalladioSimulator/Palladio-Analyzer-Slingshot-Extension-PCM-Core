package org.palladiosimulator.analyzer.slingshot.behavior.annotationprocessor.onevent;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic.Kind;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import org.palladiosimulator.analyzer.slingshot.annotationprocessor.util.exceptions.IllegalAnnotationArgumentException;
import org.palladiosimulator.analyzer.slingshot.annotationprocessor.util.exceptions.ProcessException;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.EventMethod;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.OnEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.graph.DefaultEventGraph;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.graph.DotFileGraphExporter;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.graph.EventEdge;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.graph.EventGraph;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.graph.StringBasedEventNode;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.results.ResultEvent;

import com.google.auto.service.AutoService;

@AutoService(Processor.class)
public class OnEventProcessor extends AbstractProcessor {

	private Messager messager;
	private String fileOutput;
	private boolean noGraphExportation = false;

	@Override
	public Set<String> getSupportedAnnotationTypes() {
		return Set.of(OnEvent.class.getCanonicalName(), OnEvent.OnEvents.class.getCanonicalName());
	}

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.RELEASE_11;
	}

	@Override
	public Set<String> getSupportedOptions() {
		return Set.of("export", "noexport");
	}

	@Override
	public synchronized void init(final ProcessingEnvironment processingEnv) {
		super.init(processingEnv);

		this.messager = processingEnv.getMessager();
		this.fileOutput = processingEnv.getOptions().getOrDefault("export", "EventGraph.dot");
		this.noGraphExportation = processingEnv.getOptions().getOrDefault("noexport", "false").equals("true");
	}

	@Override
	public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
		final EventGraph eventGraph = new DefaultEventGraph();

		for (final Element element : roundEnv.getElementsAnnotatedWith(OnEvent.class)) {
			final TypeElement annotatedClassElement = (TypeElement) element;

			final OnEventsModel onEventsModel = new OnEventsModel(annotatedClassElement);
			final List<ExecutableElement> methods = ElementFilter
			        .methodsIn(annotatedClassElement.getEnclosedElements())
			        .stream()
			        .filter(method -> method.getAnnotation(EventMethod.class) != null
			                || method.getSimpleName().toString().startsWith("on"))
			        .collect(Collectors.toList());

			for (final ExecutableElement method : methods) {
				try {
					checkMethod(method);
					checkContractExists(method, onEventsModel);
				} catch (final ProcessException e) {
					e.showError(messager);
				}
			}

			for (final OnEventModel model : onEventsModel.getModels()) {
				try {
					checkMethodExists(methods, model);
					checkWhenValueIsNotDESEvent(model);
					checkThenValueIsConcrete(model);
					addToGraph(eventGraph, model);
				} catch (final ProcessException e) {
					e.showError(messager);
				}
			}

		}

		exportGraph(eventGraph);

		return false;
	}

	private void checkWhenValueIsNotDESEvent(final OnEventModel model) throws ProcessException {
		if (model.getWhenElement().getQualifiedName().toString().equals(DESEvent.class.getName())) {
			throw new IllegalAnnotationArgumentException(model.getAnnotatedClassElement(), model.getAnnotation(),
			        "Contract must have a concrete event other than DESEvent.");
		}
	}

	private void checkThenValueIsConcrete(final OnEventModel model) {
		final boolean thenElementIsDESEvent = model.getThenElements().stream()
		        .anyMatch(typeElement -> typeElement.getQualifiedName().toString().equals(DESEvent.class.getName()));

		if (thenElementIsDESEvent) {
			messager.printMessage(Kind.WARNING,
			        "Contract underspecified: The contract specifies that any event can be published.",
			        model.getAnnotatedClassElement(), model.getAnnotation());
		}
	}

	private void exportGraph(final EventGraph eventGraph) {
		if (!this.noGraphExportation) {
			try {
				final FileObject fileObject = processingEnv.getFiler().createResource(StandardLocation.SOURCE_OUTPUT,
				        "",
				        this.fileOutput);

				eventGraph.exportGraph(new DotFileGraphExporter(fileObject.openWriter()));
			} catch (final IOException e) {
				messager.printMessage(Kind.WARNING,
				        "Couldn't export the EventGraph.dot due to an IO Exception: " + e.getMessage());
			}
		}
	}

	private void addToGraph(final EventGraph eventGraph, final OnEventModel model) {
		final String when = model.getWhenElement().getSimpleName().toString();
		final StringBasedEventNode whenEventNode = new StringBasedEventNode(when);
		eventGraph.addNode(whenEventNode);

		for (final TypeElement typeElement : model.getThenElements()) {
			final String then = typeElement.getSimpleName().toString();
			final StringBasedEventNode thenEventNode = new StringBasedEventNode(then);
			eventGraph.addEdge(new EventEdge(whenEventNode, thenEventNode));
		}
	}

	private void checkMethodExists(final List<ExecutableElement> methods, final OnEventModel model)
	        throws ProcessException {

		for (final ExecutableElement method : methods) {
			final TypeElement parameterType = checkParameterCorrectType(method);
			if (parameterType.getQualifiedName().equals(model.getWhenElement().getQualifiedName())) {
				return;
			}
		}

		throw new IllegalAnnotationArgumentException(model.getAnnotatedClassElement(), model.getAnnotation(),
		        "The method for this contract does not exist.");
	}

	private OnEventModel checkContractExists(final ExecutableElement element, final OnEventsModel models)
	        throws ProcessException {
		assert element.getParameters().size() == 1;

		final TypeElement parameterType = checkParameterCorrectType(element);

		final Optional<OnEventModel> optionalModel = models.getModels().stream()
		        .filter(model -> parameterType.getQualifiedName().equals(model.getWhenElement().getQualifiedName()))
		        .findFirst();

		return optionalModel.orElseThrow(() -> new ProcessException(element, String.format(
		        "Method handler does not have a corresponding contract: The contract with 'when = %s' is missing",
		        parameterType.getQualifiedName().toString())));
	}

	private TypeElement checkParameterCorrectType(final ExecutableElement element) throws ProcessException {
		assert element.getParameters().size() == 1;

		final VariableElement parameter = element.getParameters().get(0);
		final TypeMirror parameterType = parameter.asType();

		if (parameterType.getKind() != TypeKind.DECLARED) {
			throw new ProcessException(element,
			        String.format("The type must be a class implementing DESEvent, but here it is %s instead.",
			                parameterType.getKind()));
		}

		final DeclaredType declaredType = (DeclaredType) parameterType;
		final TypeElement typeElement = (TypeElement) declaredType.asElement();

		return typeElement;
	}

	private void checkMethod(final ExecutableElement method) throws ProcessException {
		if (!checkMethodParameter(method) || !checkMethodReturnType(method)) {
			throw new ProcessException(method,
			        "EventHandler method must have a return type of ResultEvent and a single parameter");
		}
	}

	private boolean checkMethodParameter(final ExecutableElement method) {
		return method.getParameters().size() == 1;
	}

	private boolean checkMethodReturnType(final ExecutableElement method) {
		final TypeMirror returnMirror = method.getReturnType();

		if (returnMirror.getKind() != TypeKind.DECLARED) {
			return false;
		}

		final DeclaredType returnType = (DeclaredType) returnMirror;
		final TypeElement returnClass = (TypeElement) returnType.asElement();

		final String qualifiedName = returnClass.getQualifiedName().toString();

		return qualifiedName.equals(ResultEvent.class.getName());
	}
}
