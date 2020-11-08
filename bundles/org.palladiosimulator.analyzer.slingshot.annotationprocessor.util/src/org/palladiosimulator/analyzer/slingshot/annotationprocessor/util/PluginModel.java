package org.palladiosimulator.analyzer.slingshot.annotationprocessor.util;

import java.io.File;
import java.io.IOException;

import javax.annotation.processing.Filer;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
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
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.xml.sax.SAXException;

import com.google.common.base.Preconditions;

/**
 * This class represents a typical {@code plugin.xml} file. Other than just
 * reading the file, it also can manipulate the existing file or manipulate it
 * if it already exists.
 * 
 * @author Julijan Katic
 */
public class PluginModel {

	/** The standard file path pointing to the Eclipse {@code plugin.xml}. */
	public static final String STANDARD_PLUGIN_FILE_PATH = "plugin.xml";

	/** The document XML element of the {@code plugin.xml} file. */
	private final Document doc;

	private final FileObject fileObject;

	/**
	 * Instanciates the model representation of a certain plugin file. If the file
	 * doesn't exist, then a new file corresponding to the file path will be
	 * created.
	 * 
	 * This constructor is typically used only for testing purposes, as the standard
	 * path for every eclipse plugin is {@code /plugin.xml}.
	 * 
	 * @param pluginFilePath The non-null, non-empty plugin file path.
	 * @param filer          The non-null filer object from which the resource can
	 *                       either be written into or created.
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 */
	public PluginModel(final String pluginFilePath, final Filer filer)
	        throws ParserConfigurationException, SAXException, IOException {
		Preconditions.checkNotNull(pluginFilePath, "The pluginFilePath must not be null.");
		Preconditions.checkArgument(pluginFilePath.isEmpty(), "The pluginFilePath must not be empty.");
		Preconditions.checkNotNull(filer);

		final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		final DocumentBuilder db = dbf.newDocumentBuilder();
		final File expectedFile = new File(pluginFilePath);

		if (expectedFile.exists()) {
			this.doc = db.parse(expectedFile);
		} else {
			this.doc = db.newDocument();
			createXMLDeclarationsAndProcessingInstructions();
		}

		this.fileObject = filer.createResource(StandardLocation.SOURCE_PATH, "", pluginFilePath);
	}

	/**
	 * Instanciates the plugin model with the {@link #STANDARD_PLUGIN_FILE_PATH}.
	 * 
	 * @param filer The filer object from which the {@code plugin.xml} file can be
	 *              manipulated/created.
	 * 
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * 
	 * @see #PluginModel(String)
	 */
	public PluginModel(final Filer filer) throws ParserConfigurationException, SAXException, IOException {
		this(STANDARD_PLUGIN_FILE_PATH, filer);
	}

	/**
	 * Helper method to create the standard declarations and processing instructions
	 * of the {@code plugin.xml}, and also already provides the plugin root node:
	 * 
	 * <pre>
	 * <code>
	 * {@code
	 * <?xml version="1.0" encoding="UTF-8"?>
	 * <?eclipse version="[the Eclipse version number]"?>
	 * <plugin>...</plugin>
	 * }
	 * </code>
	 * </pre>
	 */
	private void createXMLDeclarationsAndProcessingInstructions() {
		/* FIXME: Hard-Coded Eclipse Version */
		final ProcessingInstruction pi = doc.createProcessingInstruction("eclipse", "version=\"3.8\"");
		doc.setXmlStandalone(true);
		doc.appendChild(pi);

		final Element pluginElement = doc.createElement("plugin");
		doc.appendChild(pluginElement);
	}

	/**
	 * Creates a new "extension" element and inserts it into the right place of the
	 * file. Nothing will happen, however, if the extension declaration already
	 * exists in the file and {@code null} will be returned.
	 * 
	 * This has typically the following form:
	 * 
	 * <pre>
	 * <code>
	 * {@code
	 * <extension id="[the extension point id]">
	 *  <[basedOnNode] [basedOnAttribute]="[implementationPath]" />
	 * </extension>
	 * }
	 * </code>
	 * </pre>
	 * 
	 * @param extensionPointId   The id of the extension point onto which this
	 *                           module extends.
	 * @param basedOnNode        The name of the node that contains the information
	 *                           of the implementation.
	 * @param basedOnAttribute   The attribute of basedOnNode.
	 * @param implementationPath The path to the implementation in terms of Java
	 *                           package path.
	 * @return The extension Element that has been created. If it was already
	 *         present before this method invocation, then {@code null} will be
	 *         returned.
	 */
	public Element createExtensionElement(final String extensionPointId,
	        final String basedOnNode, final String basedOnAttribute, final String implementationPath) {

		if (this.elementExists(extensionPointId, basedOnNode, basedOnAttribute, implementationPath)) {
			return null;
		}

		final Element extensionElement = doc.createElement("extension");
		extensionElement.setAttribute("point", extensionPointId);

		final Element basedOnElement = doc.createElement(basedOnNode);
		basedOnElement.setAttribute(basedOnAttribute, implementationPath);

		extensionElement.appendChild(basedOnElement);
		doc.getDocumentElement().appendChild(extensionElement);

		return extensionElement;
	}

	/**
	 * Returns whether the extact element "extension" with the extensionPointId
	 * exists, where it has a single child named after {@code basedOnNode} having an
	 * attribute "class" with the value {@code implementationPath}. If so, then it
	 * returns {@code true}, otherwise will return {@code false}.
	 * 
	 * @param extensionPointId   The extension point id that the extension has to
	 *                           have.
	 * @param basedOnNode        The child node name of the extension.
	 * @param basedOnAttribute   The attribute name of the child node.
	 * @param implementationPath The attribute value of the attribute "class" of the
	 *                           basedOnNode node.
	 * @return true iff exists as described above.
	 */
	public boolean elementExists(final String extensionPointId, final String basedOnNode,
	        final String basedOnAttribute, final String implementationPath) {
		final NodeList extensionElements = doc.getElementsByTagName("extension");

		boolean answer = false;

		for (int i = 0; i < extensionElements.getLength(); i++) {
			final Node currentNode = extensionElements.item(i);
			final NamedNodeMap attributes = currentNode.getAttributes();

			final Node extensionPointIdAttr = attributes.getNamedItem("point");

			if (extensionPointIdAttr == null || !extensionPointIdAttr.getNodeValue().equals(extensionPointId)) {
				/* This node is not the right node. Continue searching. */
				continue;
			} else {
				/*
				 * Found the right "extension" element with the corresponding extensionPointId.
				 * Now look for children.
				 */
				final NodeList children = currentNode.getChildNodes();

				if (children.getLength() != 1) {
					/*
					 * In our model, the extension only contains a single client (basedOnNode)
					 * element. Here, it is not the case.
					 */
					continue;
				}

				for (final int node = 0; i < children.getLength(); i++) {
					final Node basedOnNodeNode = children.item(node);

					if (basedOnNodeNode.getNodeValue().equals(basedOnNode)) {
						/* The right Child-Node was found. Now check for the right attribute */
						final NamedNodeMap clientAttributes = basedOnNodeNode.getAttributes();
						final Node implementationPathAttr = clientAttributes.getNamedItem(basedOnAttribute);

						if (implementationPathAttr != null
						        && implementationPathAttr.getNodeValue().equals(implementationPath)) {
							/* This is the right node and therefore already exists. */
							answer = true;
							break;
						}
					}
				}

				if (answer) {
					break;
				}
			}
		}

		return answer;
	}

	/**
	 * EXPERIMENTAL. Helper method creates a new {@code <extension-point .../>} tag
	 * for the {@code plugin.xml} file.
	 * 
	 * @param extensionPointId   The id of the extension point
	 * @param extensionPointName The name of the extension point
	 * @param schemaFilePath     The path path into the extension point.
	 * @return If the extension point doesn't already exist, that the created
	 *         extension point element will be returned. However, if it's already
	 *         existing, then {@code null} will be returned.
	 */
	public Element createExtensionPointElement(final String extensionPointId, final String extensionPointName,
	        final String schemaFilePath) {
		if (extensionPointExists(extensionPointId)) {
			return null;
		}

		final Element extensionPointElement = doc.createElement("extension-point");
		extensionPointElement.setAttribute("id", extensionPointId);
		extensionPointElement.setAttribute("name", extensionPointName);
		extensionPointElement.setAttribute("schema", schemaFilePath);

		doc.getDocumentElement().appendChild(extensionPointElement);
		return extensionPointElement;
	}

	/**
	 * EXPERIMENTAL. Helper method for finding whether an extension-point with the
	 * certain extensionPointId exists.
	 * 
	 * @param extensionPointId the id for the extension-point tag to find.
	 * @return true iff the tag exists.
	 */
	private boolean extensionPointExists(final String extensionPointId) {
		final NodeList nodeList = doc.getElementsByTagName("extension-point");

		for (int i = 0; i < nodeList.getLength(); i++) {
			final Node node = nodeList.item(i);
			final NamedNodeMap attributes = node.getAttributes();

			if (attributes.getNamedItem("id").getNodeValue().equals(extensionPointId)) {
				/* Found an extension-point with the same id. */
				return true;
			}
		}

		return false;
	}

	/**
	 * Writes this model representation into the specified path.
	 * 
	 * @throws TransformerException
	 * @throws IOException
	 */
	public void writeDocument() throws TransformerException, IOException {
		final TransformerFactory tf = TransformerFactory.newInstance();
		final Transformer transformer = tf.newTransformer();

		/* Set up pretty-printer. */
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		transformer.setOutputProperty(OutputKeys.STANDALONE, "no");

		transformer.transform(new DOMSource(doc), new StreamResult(fileObject.openWriter()));
	}
}
