package org.palladiosimulator.analyzer.slingshot.ui.workflow.launcher.utils;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import org.palladiosimulator.analyzer.slingshot.ui.workflow.launcher.configurer.SingleTextModelField;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

public class ModelPathBinderModule extends AbstractModule {

	private static List<SingleTextModelField> singleTextModelFields = null;

	public ModelPathBinderModule() {
		if (singleTextModelFields == null) {
			singleTextModelFields = new ModelConfigurerRegister().getAllProviders()
			        .stream()
			        .filter(field -> field instanceof SingleTextModelField)
			        .map(field -> (SingleTextModelField) field)
			        .collect(Collectors.toList());
		}
	}

	@Override
	protected void configure() {
		for (final SingleTextModelField field : singleTextModelFields) {
			bind(Path.class).annotatedWith(Names.named(field.name())).toProvider(field);
		}
	}

	public List<SingleTextModelField> getFields() {
		return singleTextModelFields;
	}

}
