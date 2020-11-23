package org.palladiosimulator.analyzer.slingshot.behavior.annotationprocessor;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

public class Activator extends Plugin {

	public static final String PLUGIN_ID = "org.palladiosimulator.analyzer.slingshot.behavior.annotationprocessor";

	private static Activator plugin;

	public Activator() {
	}

	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	@Override
	public void stop(final BundleContext context) throws Exception {
		plugin = null;
		super.start(context);
	}

	public static Activator getDefault() {
		return plugin;
	}
}
