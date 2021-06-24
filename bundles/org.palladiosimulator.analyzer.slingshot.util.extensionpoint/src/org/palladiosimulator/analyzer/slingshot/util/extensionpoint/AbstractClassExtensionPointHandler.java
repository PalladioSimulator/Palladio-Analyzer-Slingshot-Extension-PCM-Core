package org.palladiosimulator.analyzer.slingshot.util.extensionpoint;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.RegistryFactory;
import org.osgi.framework.Bundle;

/**
 * This extension point loader loads the {@link Class} objects of the specified
 * class that bases on {@code ProviderType}. This means that unlike
 * {@link AbstractExtensionPointHandler} where instances are returned, this
 * returns a list of {@code Class<? extends ProviderType>}.
 * 
 * Use this handler if the instance is not required, but only class information,
 * or if the instance will be created in a different way (i.e. by an injector).
 * 
 * @param ProviderType the class/interface onto which the extension point
 *                     classes must be based on (by implementing or extending
 *                     them).
 * 
 * @author Julijan Katic
 */
public abstract class AbstractClassExtensionPointHandler<ProviderType>
		implements ExtensionPointHandler<Class<? extends ProviderType>> {

	private static final Logger LOGGER = Logger.getLogger(AbstractClassExtensionPointHandler.class);

	@SuppressWarnings("unchecked")
	@Override
	public List<Class<? extends ProviderType>> getAllProviders() {
		final List<Class<? extends ProviderType>> extensions = new ArrayList<>();

		final IConfigurationElement[] elements = RegistryFactory.getRegistry()
				.getConfigurationElementsFor(this.getExtensionPointId());

		for (final IConfigurationElement element : elements) {
			final String classTo = element.getAttribute(this.getExecutableExtensionName());
			final Bundle bundle = Platform.getBundle(element.getContributor().getName());

			if (classTo == null) {
				continue;
			}

			try {
				final Class<?> cls = bundle.loadClass(classTo);

				if (this.getProvidersClazz().isAssignableFrom(cls)) {
					extensions.add((Class<? extends ProviderType>) cls);
				}
			} catch (final ClassNotFoundException e) {
				LOGGER.error("The class of the extension point couldn't be found.", e);
			}

		}

		return extensions;
	}

	/**
	 * Returns the class of the provider itself.
	 * 
	 * @return The class type of the provider itself.
	 */
	protected abstract Class<? extends ProviderType> getProvidersClazz();

}
