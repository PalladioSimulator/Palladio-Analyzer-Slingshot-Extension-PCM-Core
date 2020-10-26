package org.palladiosimulator.analyzer.slingshot.ui.workflow.launcher.configurer;

import java.nio.file.Path;

import javax.inject.Provider;

import org.palladiosimulator.analyzer.slingshot.module.models.AbstractModelProvider;

import com.google.inject.name.Names;

/**
 * An abstract class that provides information about how a text field can be
 * mapped into a {@link Path} variable. This allows the usage of
 * 
 * <pre>
 * <code>
 * {@code @Provides}
 * public SomeModel provideSomeModel(@Named([name]) final Path path)...
 * </code>
 * </pre>
 * 
 * The {@code [name]} is provided by {@link #name()}.
 * 
 * @author Julijan Katic
 */
public abstract class ModelPathBinder extends AbstractModelProvider implements Provider<Path> {

	/**
	 * Returns the name for the path to load the right one.
	 * 
	 * @return The non-null, non-empty and unique name for this field.
	 */
	public abstract String name();

	@Override
	protected void configure() {
		bind(Path.class).annotatedWith(Names.named(this.name())).toProvider(this);
	}

}
