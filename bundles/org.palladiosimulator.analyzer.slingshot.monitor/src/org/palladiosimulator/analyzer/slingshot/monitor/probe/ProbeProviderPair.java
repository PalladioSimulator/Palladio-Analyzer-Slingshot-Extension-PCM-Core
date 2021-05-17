package org.palladiosimulator.analyzer.slingshot.monitor.probe;

import java.lang.reflect.Method;

public final class ProbeProviderPair {

	private final Object instance;
	private final Method providerMethod;

	public ProbeProviderPair(final Object instance, final Method providerMethod) {
		super();
		this.instance = instance;
		this.providerMethod = providerMethod;
	}

	/**
	 * @return the instance
	 */
	public Object getInstance() {
		return this.instance;
	}

	/**
	 * @return the providerMethod
	 */
	public Method getProviderMethod() {
		return this.providerMethod;
	}

}
