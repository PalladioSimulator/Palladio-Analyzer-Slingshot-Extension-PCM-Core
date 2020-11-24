package org.palladiosimulator.analyzer.slingshot.util.extensionpoint;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.RegistryFactory;

/**
 * This class is a standard extension point handler that uses the Eclipse
 * framework in order to find them. This will try to get the
 * <strong>instances</strong> of the extension points. Therefore, the classes
 * that were provided by the extension must have a public, default constructor.
 * 
 * <p>
 * If only the class reference (but not the instance) is needed, see
 * {@link AbstractClassExtensionPointHandler}.
 * 
 * @param <ProviderType> The type of the provider. This is typically a interface
 *                       that an extension has to implement.
 * @author Julijan Katic
 */
public abstract class AbstractExtensionPointHandler<ProviderType> implements ExtensionPointHandler<ProviderType> {

	private static final Logger LOGGER = Logger.getLogger(AbstractExtensionPointHandler.class);

	@Override
	public List<ProviderType> getAllProviders() {
		final List<ProviderType> providers = new ArrayList<>();

		try {
			final IConfigurationElement[] config = RegistryFactory.getRegistry()
			        .getConfigurationElementsFor(this.getExtensionPointId());

			for (final IConfigurationElement e : config) {
				final Object o = e.createExecutableExtension(this.getExecutableExtensionName());

				if (this.isCastable(o)) {
					LOGGER.debug("An instance of type " + this.getProvidersClazz().getSimpleName() + " was found.");

					final ProviderType provider = this.getProvidersClazz().cast(o);
					providers.add(provider);
				}
			}
		} catch (final CoreException e) {
			LOGGER.error("The extension could not be added due to an exception", e);
		}

		return providers;
	}

	/**
	 * Returns the Class of the provider type (that is typically an interface
	 * needing to be implemented by the extension). This method must not return
	 * null.
	 * 
	 * @return The non-null class object of the provider.
	 */
	protected abstract Class<? extends ProviderType> getProvidersClazz();

	/**
	 * Helper method that determines whether obj can be casted to
	 * {@link #getProvidersClazz()}
	 * 
	 * @param obj A non-null obj to be tested.
	 * @return true if obj can be casted to the type returned by
	 *         {@link #getProvidersClazz()}. Otherwise, false.
	 */
	private boolean isCastable(final Object obj) {
		if (obj == null || this.getProvidersClazz() == null) {
			throw new IllegalArgumentException("Neither obj nor getProvidersClazz() must return null!");
		}
		return this.getProvidersClazz().isAssignableFrom(obj.getClass());
	}
}