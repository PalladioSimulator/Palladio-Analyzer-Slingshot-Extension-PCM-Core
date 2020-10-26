package org.palladiosimulator.analyzer.slingshot.extensionpoint.annotationprocessor;

import java.io.File;
import java.io.IOException;

import javax.tools.FileObject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.ProcessingInstruction;
import org.xml.sax.SAXException;

/**
 * This is a utility class for manipulating the {@code plugin.xml} files.
 * 
 * @author Julijan Katic
 * @deprecated Use {@link PluginModel} instead.
 */
@Deprecated
public class PluginFiler {

	public static final String PLUGIN_FILE = "plugin.xml";

	public static Document getPluginDocument() throws SAXException, IOException, ParserConfigurationException {
		return getPluginDocument(PLUGIN_FILE);
	}

	public static Document getPluginDocument(final String pluginFilePath)
			throws SAXException, IOException, ParserConfigurationException {
		final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		final DocumentBuilder db = dbf.newDocumentBuilder();

		final File expectedFile = new File(pluginFilePath);

		final Document doc;

		if (expectedFile.exists()) {
			doc = db.parse(expectedFile);
		} else {
			doc = db.newDocument();
			createXMLDeclarationsAndProcessingInstructions(doc);
		}

		return doc;
	}

	private static void createXMLDeclarationsAndProcessingInstructions(final Document doc) {
		/* TODO: Hard-Coded Eclipse Version */
		final ProcessingInstruction pi = doc.createProcessingInstruction("eclipse", "version=\"3.8\"");
		doc.setXmlStandalone(true);
		doc.appendChild(pi);

		final Element pluginElement = doc.createElement("plugin");
		doc.appendChild(pluginElement);
	}

	public static Element createExtensionElement(final Document pluginDoc, final String extensionPointId,
			final String basedOnNode, final String implementationPath) {
		final Element extensionElement = pluginDoc.createElement("extension");
		extensionElement.setAttribute("point", extensionPointId);

		final Element basedOnElement = pluginDoc.createElement(basedOnNode);
		basedOnElement.setAttribute("class", implementationPath);

		extensionElement.appendChild(basedOnElement);
		pluginDoc.getDocumentElement().appendChild(extensionElement);

		return extensionElement;
	}

	public static void writeDocument(final Document pluginDoc, final FileObject fileObject)
			throws TransformerException, IOException {
		final TransformerFactory tf = TransformerFactory.newInstance();
		final Transformer transformer = tf.newTransformer();

		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		transformer.setOutputProperty(OutputKeys.STANDALONE, "no");

		transformer.transform(new DOMSource(pluginDoc), new StreamResult(fileObject.openWriter()));
	}
}
