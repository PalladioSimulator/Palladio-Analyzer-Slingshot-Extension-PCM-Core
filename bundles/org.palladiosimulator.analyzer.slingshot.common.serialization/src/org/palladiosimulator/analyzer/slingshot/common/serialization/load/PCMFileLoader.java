package org.palladiosimulator.analyzer.slingshot.common.serialization.load;

import java.nio.file.Path;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

public class PCMFileLoader {

	static {
		EMFResourceSetInitializerHelper.initEMF();
	}

	public static <T> T load(final Path filePath) {
		final URI uri = createSuitedURI(filePath);
		return load(uri);
	}

	@SuppressWarnings("unchecked")
	public static <T extends EObject> T load(final URI uri) {
		final ResourceSet resourceSet = new ResourceSetImpl();
		final Resource modelResource = resourceSet.getResource(uri, true);
		return (T) modelResource.getContents().get(0);
	}

	private static boolean isPlatformPath(final Path path) {
		return path.toString().startsWith("platform:");
	}

	/**
	 * Helper method for creating a suited URI object for the model resource. It has
	 * to be used; otherwise, if path is a platform URI, a FileNotFoundException
	 * will be thrown.
	 * 
	 * @param path
	 * @return a suited URI
	 */
	private static URI createSuitedURI(final Path path) {
		URI uri;

		if (isPlatformPath(path)) {
			uri = URI.createURI(path.toString());
		} else {
			uri = URI.createFileURI(path.toString());
		}

		return uri;
	}
}
