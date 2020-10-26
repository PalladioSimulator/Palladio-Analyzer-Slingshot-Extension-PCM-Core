package org.palladiosimulator.analyzer.slingshot.extensionpoint.annotationprocessor.extensionpoint;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.text.StringSubstitutor;
import org.palladiosimulator.analyzer.slingshot.extensionpoint.annotationprocessor.extensionpoint.SchemaModel.SchemaFileAlreadyExists;
import org.w3c.dom.Document;

import com.google.common.base.Preconditions;

public class SchemaModel {

	private final Document doc;
	private final String path;
	
	public SchemaModel(final String path, final ExtensionPointModel model) throws ParserConfigurationException, SchemaFileAlreadyExists {
		Preconditions.checkNotNull(model);
		
		String schemaFilePath = path;
		if (schemaFilePath == null || schemaFilePath.isEmpty()) {
			/* Then use the standard path */
			schemaFilePath = model.getExtensionPointId();
		}
		
		final File schemaFile = new File(schemaFilePath);
		
		if (schemaFile.exists()) {
			/* Then we don't need to create this. */
			throw new SchemaFileAlreadyExists();
		}
		
		this.path = path;
		
		final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		final DocumentBuilder db = dbf.newDocumentBuilder();
		
		this.doc = db.newDocument();
		
		createDocument(model);
	}
	
	public SchemaModel(final ExtensionPointModel model) throws ParserConfigurationException, SchemaFileAlreadyExists {
		this(null, model);
	}
	
	private void createDocument(ExtensionPointModel model) {
		doc.setXmlVersion("1.0");
		
		StringSubstitutor s;
	}

	public static class SchemaFileAlreadyExists extends Exception {}
}
