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

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
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

/**
 * This processor processes all {@link OnEvent} and {@link OnEvent.OnEvents}
 * annotations in such a way that a compile-time EventGraph can be exported and
 * that all compile-time validations are done.
 * 
 * It validates the following constraints on {@link OnEvent}:
 * <ul>
 * <li>It checks that for each EventHandler there is a corresponding contract on
 * the class definition.
 * <li>Also, it checks that for each contract there is a corresponding event
 * handler.
 * <li>It checks whether the input event ({@link OnEvent#when()}) is exactly
 * {@link DESEvent} which is prohibited.
 * <li>It checks whether the output events list ({@link OnEvent#then()}) has
 * {@link DESEvent} contained which is not recommended.
 * </ul>
 * 
 * This processor also allows the following options:
 * <ul>
 * <li>{@code export}: String -- Where to export the graph file. Default value
 * is "EventGraph.dot".
 * <li>{@code noexport}: Boolean -- If there shouldn't be any graph exported.
 * Default value is "false".
 * </ul>
 * 
 * @author Julijan Katic
 */
@AutoService(Processor.class)
public class OnEventProcessor extends AbstractProcessor {

	private final Logger LOGGER = Logger.getLogger(OnEventProcessor.class);

	private Messager messager;
	private String fileOutput;
	private boolean noGraphExportation = false;

	public OnEventProcessor() {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}

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

		LOGGER.debug("OnEventProcessor initialized");
	}

	@Override
	public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
		final EventGraph eventGraph = new DefaultEventGraph();
		LOGGER.debug("Starting processing");

		for (final Element element : roundEnv.getElementsAnnotatedWith(OnEvent.OnEvents.class)) {
			final TypeElement annotatedClassElement = (TypeElement) element;
			LOGGER.debug("Found element: " + annotatedClassElement.getSimpleName().toString());
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

	/**
	 * Helper method for checking if the input event of the contract is exactly
	 * {@link DESEvent}. If so, it will throw the {@link ProcessException}.
	 * 
	 * @param model The model of the corresponding contract.
	 * @throws IllegalAnnotationArgumentException If {@link OnEvent#when()} equals
	 *                                            to {@code DESEvent.class}.
	 */
	private void checkWhenValueIsNotDESEvent(final OnEventModel model) throws IllegalAnnotationArgumentException {
		if (model.getWhenElement().getQualifiedName().toString().equals(DESEvent.class.getName())) {
			throw new IllegalAnnotationArgumentException(model.getAnnotatedClassElement(), model.getAnnotation(),
			        "Contract must have a concrete event other than DESEvent.");
		}
	}

	/**
	 * Checks if the output events on the contract has a {@link DESEvent} contained.
	 * If so, a warning will be displayed.
	 * 
	 * @param model The model of the corresponding contract.
	 */
	private void checkThenValueIsConcrete(final OnEventModel model) {
		final boolean thenElementIsDESEvent = model.getThenElements().stream()
		        .anyMatch(typeElement -> typeElement.getQualifiedName().toString().equals(DESEvent.class.getName()));
		LOGGER.debug(
		        "OnEventModel checking: " + model.getWhenElement().getQualifiedName() + " -- " + thenElementIsDESEvent);
		if (thenElementIsDESEvent) {
			messager.printMessage(Kind.WARNING,
			        "Contract underspecified: The contract specifies that any event can be published.",
			        model.getAnnotatedClassElement(), model.getAnnotation());
		}
	}

	/**
	 * Exports the event graph. If there is an {@link IOException}, a compiler
	 * warning will be displayed. It will not export the graph if {@code noexport}
	 * option is set to {@code true}. The file name is set to {@code export}'s
	 * value.
	 * 
	 * @param eventGraph The graph containing all the events and edges to be
	 *                   exported.
	 */
	private void exportGraph(final EventGraph eventGraph) {
		if (!this.noGraphExportation) {
			try {
				final FileObject fileObject = processingEnv.getFiler().createResource(StandardLocation.SOURCE_OUTPUT,
				        "",
				        this.fileOutput);
				LOGGER.debug("exporting graph");
				eventGraph.exportGraph(new DotFileGraphExporter(fileObject.openWriter()));
			} catch (final IOException e) {
				messager.printMessage(Kind.WARNING,
				        "Couldn't export the EventGraph.dot due to an IO Exception: " + e.getMessage());
			}
		}
	}

	/**
	 * Adds edges and nodes (if there isn't already one) to the graph according to
	 * the model. Each edge will go from the contract's {@code when} to one of
	 * {@code then}'s element. At the end, there will be an edge to every element of
	 * {@code then}.
	 * 
	 * @param eventGraph The graph onto which to add the nodes and edges.
	 * @param model      The model of the contract to add to the graph.
	 */
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

	/**
	 * Checks whether a method exists for a certain contract. A method
	 * <em>exists</em> if it has a single parameter of that type specified in the
	 * contract's {@code when} value, and the return type is {@code ResultEvent}
	 * with any generic type. If such a method does not exist,
	 * {@link ProcessException} will be thrown.
	 * 
	 * 
	 * @param methods The methods of the class onto which the contract is specified.
	 * @param model   The model of the corresponding contract.
	 * @throws ProcessException if such a method does not exist.
	 */
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

	/**
	 * Checks for each event handler if it has a corresponding contract specified.
	 * The contract has to have the {@code when} value to be set to the type of the
	 * parameter of the method. It also checks if the event handler has a correct
	 * parameter type. If such a contract does not exist, then
	 * {@link ProcessException} will be thrown.
	 * 
	 * @param element The event handler with the single parameter.
	 * @param models  The
	 * @return
	 * @throws ProcessException
	 */
	private OnEventModel checkContractExists(final ExecutableElement element, final OnEventsModel models)
	        throws ProcessException {
		assert element.getParameters().size() == 1;

		final TypeElement parameterType = checkParameterCorrectType(element);

		final Optional<OnEventModel> optionalModel = models.getModels().stream()
		        .filter(model -> {
			        final String parameterTypeName = parameterType.getQualifiedName().toString();
			        final String modelTypeName = model.getWhenElement().getQualifiedName().toString();
			        LOGGER.debug("parameterType: " + parameterTypeName + " --- modelType: " + modelTypeName);
			        return parameterTypeName.equals(modelTypeName);
		        })
		        .findFirst();

		return optionalModel.orElseThrow(() -> new ProcessException(element, String.format(
		        "Method handler does not have a corresponding contract: The contract with 'when = %s' is missing",
		        parameterType.getQualifiedName().toString())));
	}

	/**
	 * Checks whether the event handler has a correct parameter type. The type is
	 * correct if it is a subtype of {@link DESEvent}. It also returns the type for
	 * further computation.
	 * 
	 * @param element The method to check.
	 * @return The actual type of the method if it is a subtype of {@link DESEvent}.
	 * @throws ProcessException if it is not a subtype of {@link DESEvent}.
	 */
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

	/**
	 * Checks that the event handler has a single parameter and the correct return
	 * type. The correct return type is {@link ResultEvent}.
	 * 
	 * @param method The method to check.
	 * @throws ProcessException if it has more than one parameter or a different
	 *                          return type.
	 */
	private void checkMethod(final ExecutableElement method) throws ProcessException {
		if (!checkMethodParameter(method) || !checkMethodReturnType(method)) {
			throw new ProcessException(method,
			        "EventHandler method must have a return type of ResultEvent and a single parameter");
		}
	}

	/**
	 * Returns whether the method has a single parameter.
	 * 
	 * @param method The method to check.
	 * @return true iff it has a single parameter.
	 */
	private boolean checkMethodParameter(final ExecutableElement method) {
		return method.getParameters().size() == 1;
	}

	/**
	 * Returns whether the method has a return type of {@link ResultEvent}.
	 * 
	 * @param method The method to check.
	 * @return true iff it has the correct return type.
	 */
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
