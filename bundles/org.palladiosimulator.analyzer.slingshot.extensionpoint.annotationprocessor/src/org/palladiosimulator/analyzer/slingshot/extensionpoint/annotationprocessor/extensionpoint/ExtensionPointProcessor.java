package org.palladiosimulator.analyzer.slingshot.extensionpoint.annotationprocessor.extensionpoint;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.nio.file.Files;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic.Kind;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.text.StringSubstitutor;
import org.palladiosimulator.analyzer.slingshot.annotations.ExtensionPoint;
import org.palladiosimulator.analyzer.slingshot.extensionpoint.annotationprocessor.IllegalAnnotationArgumentException;
import org.palladiosimulator.analyzer.slingshot.extensionpoint.annotationprocessor.PluginModel;
import org.xml.sax.SAXException;

/**
 * A processor for processing the {@link ExtensionPoint} annotations. This will
 * create the XSD schema file that describes the extension-point and adds the
 * corresponding information in the {@code plugin.xml} file.
 * 
 * @author Julijan Katic
 */
public class ExtensionPointProcessor extends AbstractProcessor {

	private Messager messager;
	private Filer filer;

	private final String pluginResourceFilePath;
	private PluginModel pluginModel;

	public ExtensionPointProcessor() {
		this(PluginModel.STANDARD_PLUGIN_FILE_PATH);
	}

	public ExtensionPointProcessor(final String path) {
		this.pluginResourceFilePath = path;
	}

	@Override
	public Set<String> getSupportedAnnotationTypes() {
		return Set.of(ExtensionPoint.class.getCanonicalName());
	}

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latestSupported();
	}

	@Override
	public synchronized void init(final ProcessingEnvironment processingEnv) {
		super.init(processingEnv);

		this.messager = processingEnv.getMessager();
		this.filer = processingEnv.getFiler();
	}

	@Override
	public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {

		try {
			this.pluginModel = new PluginModel(this.pluginResourceFilePath, this.filer);

			final Set<TypeElement> annotatedClasses = ElementFilter
					.typesIn(roundEnv.getElementsAnnotatedWith(ExtensionPoint.class));

			for (final TypeElement classElement : annotatedClasses) {
				final ExtensionPointModel model = new ExtensionPointModel(classElement);
				createSchemaFile(model);
				appendToPluginFile(model);
			}
		} catch (ParserConfigurationException | SAXException | IOException e) {
			messager.printMessage(Kind.ERROR, "The plugin file couldn't be created or read: " + e.getMessage());
		} catch (final IllegalAnnotationArgumentException exception) {
			messager.printMessage(Kind.ERROR, exception.getMessage(), exception.getAnnotatedElement());
		}

		return false;
	}

	/**
	 * Helper method that creates the schema file. The schema file will be saved
	 * under the form {@code schema/<extension-point-id>.exsd}.
	 * 
	 * @param model The model that contains the information of the
	 *              {@link ExtensionPoint} annotation.
	 * @throws IOException if the file couldn't be created.
	 */
	private void createSchemaFile(final ExtensionPointModel model) throws IOException {
		final URL templateUrl = getClass().getResource("resource/GeneralSchema.template");
		final File templateFile = new File(templateUrl.getPath());

		final String template = Files.readString(templateFile.toPath());
		final StringSubstitutor substitutor = new StringSubstitutor(model.getStringMap());

		final FileObject fileObject = filer.createResource(StandardLocation.SOURCE_PATH, "",
				getSchemaFilePath(model));

		Writer writer = null;

		try {
			writer = fileObject.openWriter();
			writer.write(substitutor.replace(template));
		} finally {
			if (writer != null) {
				writer.close();
			}
		}

	}

	/**
	 * Appends to the {@code plugin.xml} file the necessary information in order to
	 * register the extension point.
	 * 
	 * @param model The model containing the information.
	 */
	private void appendToPluginFile(final ExtensionPointModel model) {
		pluginModel.createExtensionPointElement(model.getExtensionPointId(), model.getExtensionPointName(),
				getSchemaFilePath(model));
	}

	/**
	 * Helper method for returning the path to the schema file.
	 */
	private String getSchemaFilePath(final ExtensionPointModel model) {
		return String.format("schema/%s.exsd", model.getExtensionPointId());
	}
}
