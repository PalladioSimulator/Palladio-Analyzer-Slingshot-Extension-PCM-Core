package org.palladiosimulator.analyzer.slingshot.extensionpoint.annotationprocessor;

import java.io.IOException;

import javax.tools.FileObject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * This is a utility class for manipulating the {@code plugin.xml} files.
 * 
 * @author Julijan Katic
 */
public class PluginFiler {

	public static final String PLUGIN_FILE = "plugin.xml";

	public static Document getPluginDocument() throws SAXException, IOException, ParserConfigurationException {
		return getPluginDocument(PLUGIN_FILE);
	}

	public static Document getPluginDocument(final String pluginFilePath)
			throws SAXException, IOException, ParserConfigurationException {
		final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		final DocumentBuilder db = dbf.newDocumentBuilder();
		final Document doc = db.parse(pluginFilePath);

		return doc;
	}

	public static Element createExtensionElement(final Document pluginDoc, final String extensionPointId,
			final String basedOnNode, final String implementationPath) {
		final Element extensionElement = pluginDoc.createElement("extension");
		extensionElement.setAttribute("plugin", extensionPointId);

		final Element basedOnElement = pluginDoc.createElement(basedOnNode);
		basedOnElement.setAttribute("class", implementationPath);

		extensionElement.appendChild(basedOnElement);
		pluginDoc.appendChild(extensionElement);

		return extensionElement;
	}

	public static void writeDocument(final Document pluginDoc, final FileObject fileObject)
			throws TransformerException, IOException {
		final TransformerFactory tf = TransformerFactory.newInstance();
		final Transformer transformer = tf.newTransformer();

		transformer.transform(new DOMSource(pluginDoc), new StreamResult(fileObject.openWriter()));
	}
}
