package org.palladiosimulator.analyzer.slingshot.provider.model.usagemodel;

import org.palladiosimulator.analyzer.slingshot.common.constants.model.ModelFileTypeConstants;
import org.palladiosimulator.analyzer.slingshot.ui.workflow.launcher.configurer.SingleTextModelField;

public class RequiredUsageModelTextForm extends SingleTextModelField {

	public static final String PATH_BINDING_ID = "usageModelPath";

	public RequiredUsageModelTextForm() {
		super(TextFieldModel.of("Usage File", "Select Usage File", ModelFileTypeConstants.USAGEMODEL_FILE_EXTENSION));
	}

	@Override
	public String fieldName() {
		return "Usage File";
	}

	@Override
	public String name() {
		return ModelFileTypeConstants.USAGE_FILE;
	}

}
