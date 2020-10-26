package org.palladiosimulator.analyzer.slingshot.provider.model.allocation;

import org.palladiosimulator.analyzer.slingshot.common.constants.model.ModelFileTypeConstants;
import org.palladiosimulator.analyzer.slingshot.ui.workflow.launcher.configurer.SingleTextModelField;

public class RequiredAllocationTextForm extends SingleTextModelField {

	public static final String PATH_BINDER_ID = "allocationModelPath";

	public RequiredAllocationTextForm() {
		super(TextFieldModel.of("Allocation File", "Select Allocation File",
		        ModelFileTypeConstants.ALLOCATION_FILE_EXTENSION));
	}

	@Override
	public String fieldName() {
		return "Allocation File";
	}

	@Override
	public String name() {
		return PATH_BINDER_ID;
	}

}
