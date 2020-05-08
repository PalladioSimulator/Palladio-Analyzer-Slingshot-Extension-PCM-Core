package org.palladiosimulator.analyzer.slingshot.common.serialization.load;

import java.nio.file.Path;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

public class PCMFileLoader {

	@SuppressWarnings("unchecked")
	public <T> T load(final Path filePath) {
		EMFResourceSetInitializerHelper.initEMF(filePath);
		ResourceSet resourceSet = new ResourceSetImpl();
		URI fileURI = URI.createFileURI(filePath.toString());
		Resource modelResource = resourceSet.getResource(fileURI, true);
		return (T) modelResource.getContents().get(0);
	}
	
}
