package org.palladiosimulator.analyzer.slingshot.common.serialization.load;

import java.nio.file.Path;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.palladiosimulator.pcm.usagemodel.UsageModel;

public class UsageModelFileLoader {
	
	public UsageModel load(final Path filePath) {
		EMFResourceSetInitializerHelper.initEMF(filePath);
		ResourceSet resourceSet = new ResourceSetImpl();
		URI fileURI = URI.createFileURI(filePath.toString());
		Resource usageModelResource = resourceSet.getResource(fileURI, true);
		return (UsageModel)usageModelResource.getContents().get(0);
	}

}
