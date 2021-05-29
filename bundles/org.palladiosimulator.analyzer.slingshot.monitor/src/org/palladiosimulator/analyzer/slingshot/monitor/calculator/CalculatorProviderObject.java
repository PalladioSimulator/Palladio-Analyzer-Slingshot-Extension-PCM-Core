package org.palladiosimulator.analyzer.slingshot.monitor.calculator;

import org.palladiosimulator.analyzer.slingshot.util.extensionpoint.AbstractClassExtensionPointHandler;

public final class CalculatorProviderObject extends AbstractClassExtensionPointHandler<Object> {

	@Override
	public String getExtensionPointId() {
		return "org.palladiosimulator.analyzer.slingshot.monitor.calculators";
	}

	@Override
	public String getExecutableExtensionName() {
		return "providers";
	}

	@Override
	protected Class<? extends Object> getProvidersClazz() {
		return Object.class;
	}

}
