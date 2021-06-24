package org.palladiosimulator.analyzer.slingshot.monitor.calculator;

import org.palladiosimulator.analyzer.slingshot.util.extensionpoint.AbstractClassExtensionPointHandler;

public final class CalculatorProviderObject extends AbstractClassExtensionPointHandler<AbstractCalculatorProviders> {

	@Override
	public String getExtensionPointId() {
		return "org.palladiosimulator.analyzer.slingshot.monitor.calculators";
	}

	@Override
	public String getExecutableExtensionName() {
		return "provider";
	}

	@Override
	protected Class<? extends AbstractCalculatorProviders> getProvidersClazz() {
		return AbstractCalculatorProviders.class;
	}

}
