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
		final ResourceSet resourceSet = new ResourceSetImpl();
		final URI uri = this.createSuitedURI(filePath);
		final Resource modelResource = resourceSet.getResource(uri, true);
		return (T) modelResource.getContents().get(0);
	}
	
	private boolean isPlatformPath(final Path path) {
		return path.toString().startsWith("platform:");
	}
	
	/**
	 * Helper method for creating a suited URI object for the
	 * model resource. It has to be used; otherwise, if path
	 * is a platform URI, a FileNotFoundException will
	 * be thrown.
	 * @param path
	 * @return a suited URI
	 */
	private URI createSuitedURI(final Path path) {
		URI uri;
		
		if (this.isPlatformPath(path)) {
			uri = URI.createURI(path.toString());
		} else {
			uri = URI.createFileURI(path.toString());
		}
		
		return uri;
	}
}
