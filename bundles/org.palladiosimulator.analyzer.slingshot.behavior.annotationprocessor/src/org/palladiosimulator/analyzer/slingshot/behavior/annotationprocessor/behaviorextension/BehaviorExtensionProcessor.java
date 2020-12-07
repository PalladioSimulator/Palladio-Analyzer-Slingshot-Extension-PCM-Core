package org.palladiosimulator.analyzer.slingshot.behavior.annotationprocessor.behaviorextension;

import java.io.IOException;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.palladiosimulator.analyzer.slingshot.annotationprocessor.util.PluginModel;
import org.palladiosimulator.analyzer.slingshot.annotationprocessor.util.exceptions.ProcessException;
import org.palladiosimulator.analyzer.slingshot.simulation.SlingshotCorePlugin;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.annotations.BehaviorExtension;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.SimulationBehaviorExtension;
import org.xml.sax.SAXException;

/**
 * This annotation processor handles all the {@link BehaviorExtension}
 * annotations. It generates / manipulates the {@code plugin.xml} in such a way
 * that a extension is created to the certain extension point of
 * {@link SlingshotCorePlugin.BEHAVIOR_EXTENSION_POINT_ID}.
 * 
 * @author Julijan Katic
 */
public class BehaviorExtensionProcessor extends AbstractProcessor {

	private static final String GENERIC_EXTENSION_POINT_ID = SlingshotCorePlugin.BEHAVIOR_EXTENSION_POINT_ID;
	private static final String GENERIC_BASED_ON_NODE = "behavior-provider";
	private static final String GENERIC_BASED_ON_ATTRIBUTE = "baseClass";

	private Types typeUtils;
	private Elements elementUtils;
	private Messager messager;
	private Filer filer;

	@Override
	public synchronized void init(final ProcessingEnvironment processingEnv) {
		super.init(processingEnv);

		this.typeUtils = processingEnv.getTypeUtils();
		this.elementUtils = processingEnv.getElementUtils();
		this.messager = processingEnv.getMessager();
		this.filer = processingEnv.getFiler();
	}

	@Override
	public Set<String> getSupportedAnnotationTypes() {
		return Set.of(BehaviorExtension.class.getName());
	}

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latest();
	}

	@Override
	public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {

		for (final TypeElement typeElement : ElementFilter
		        .typesIn(roundEnv.getElementsAnnotatedWith(BehaviorExtension.class))) {

			try {
				checkImplementsSimulationBehaviorExtension(typeElement);
				generatePluginFile(typeElement.getQualifiedName().toString());
			} catch (final ProcessException e) {
				e.showError(messager);
			} catch (ParserConfigurationException | SAXException | IOException | TransformerException e) {
				// TODO Error Handling
				e.printStackTrace();
			}

		}

		return false;
	}

	private void checkImplementsSimulationBehaviorExtension(final TypeElement typeElement) throws ProcessException {
		final TypeMirror typeSimulationBehaviorExtension = elementUtils
		        .getTypeElement(SimulationBehaviorExtension.class.getName()).asType();

		if (!typeUtils.isSubtype(typeElement.asType(), typeSimulationBehaviorExtension)) {
			throw new ProcessException(typeElement, "Classes annotated with @" + BehaviorExtension.class.getSimpleName()
			        + " must implement the interface " + SimulationBehaviorExtension.class.getSimpleName());
		}
	}

	private void generatePluginFile(final String className)
	        throws ParserConfigurationException, SAXException, IOException, TransformerException {
		final PluginModel pluginModel = new PluginModel(filer);

		pluginModel.createExtensionElement(GENERIC_EXTENSION_POINT_ID, GENERIC_BASED_ON_NODE,
		        GENERIC_BASED_ON_ATTRIBUTE, className);

		pluginModel.writeDocument();
	}
}
