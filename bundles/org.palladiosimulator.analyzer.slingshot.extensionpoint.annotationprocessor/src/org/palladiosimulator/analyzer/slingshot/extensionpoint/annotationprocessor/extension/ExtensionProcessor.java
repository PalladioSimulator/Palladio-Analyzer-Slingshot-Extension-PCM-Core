package org.palladiosimulator.analyzer.slingshot.extensionpoint.annotationprocessor.extension;

import java.io.IOException;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
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

import org.palladiosimulator.analyzer.slingshot.annotations.Extension;
import org.palladiosimulator.analyzer.slingshot.extensionpoint.annotationprocessor.PluginFiler;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * This annotation processor manipulates the {@code plugin.xml} file and
 * provides an extension to its specified extension point.
 * 
 * @author Julijan Katic
 * @see Extension
 */
public class ExtensionProcessor extends AbstractProcessor {

	private Messager messager;
	private Filer filer;

	@Override
	public Set<String> getSupportedAnnotationTypes() {
		return Set.of(Extension.class.getCanonicalName());
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

		for (final Element annotatedElement : roundEnv.getElementsAnnotatedWith(Extension.class)) {
			final TypeElement classElement = (TypeElement) annotatedElement;

			try {
				final ExtensionModel model = new ExtensionModel(classElement);

				final Document doc = PluginFiler.getPluginDocument();

				PluginFiler.createExtensionElement(doc, model.getExtensionPointId(),
						model.getExtensionPointImplementationNode(), model.getImplementation());

				PluginFiler.writeDocument(doc, filer.getResource(StandardLocation.CLASS_OUTPUT, "", "plugin.xml"));

			} catch (final IllegalArgumentException ex) {
				messager.printMessage(Kind.ERROR, ex.getMessage(), classElement);
			} catch (SAXException | IOException | ParserConfigurationException | TransformerException ex) {
				messager.printMessage(Kind.ERROR, ex.getMessage(), classElement);
			}
		}

		return false;
	}

}
