package org.palladiosimulator.analyzer.slingshot.extensionpoint.annotationprocessor.extensionannotation;

import java.io.IOException;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;
import javax.tools.StandardLocation;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.palladiosimulator.analyzer.slingshot.annotations.ExtensionPoint;
import org.palladiosimulator.analyzer.slingshot.extensionpoint.annotationprocessor.PluginFiler;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class ExtensionAnnotationProcessor extends AbstractProcessor {

	private Messager messager;

	@Override
	public Set<String> getSupportedAnnotationTypes() {
		return Set.of(ExtensionPoint.class.getCanonicalName());
	}

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.RELEASE_8;
	}

	@Override
	public synchronized void init(final ProcessingEnvironment processingEnv) {
		super.init(processingEnv);

		messager = processingEnv.getMessager();
	}

	@Override
	public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {

		for (final Element element : roundEnv.getElementsAnnotatedWith(ExtensionPoint.class)) {
			final TypeElement annotationElement = (TypeElement) element;

			for (final Element annotatedClass : roundEnv.getElementsAnnotatedWith(annotationElement)) {

				try {
					final TypeElement classElement = (TypeElement) annotatedClass;

					final ExtensionAnnotationModel model = new ExtensionAnnotationModel(annotationElement,
							classElement);

					final Document doc = PluginFiler.getPluginDocument();
					PluginFiler.createExtensionElement(doc, model.getExtensionModel().getExtensionPointId(),
							model.getExtensionModel().getExtensionPointImplementationNode(),
							model.getExtensionModel().getImplementation());

					PluginFiler.writeDocument(doc,
							processingEnv.getFiler().getResource(StandardLocation.CLASS_OUTPUT, "", "plugin.xml"));
				} catch (SAXException | IOException | ParserConfigurationException | IllegalArgumentException
						| TransformerException e) {
					messager.printMessage(Kind.ERROR, e.getMessage(), element);
				}

			}
		}

		return false;
	}

}
