package org.palladiosimulator.analyzer.slingshot.ui.workflow.launcher;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.palladiosimulator.analyzer.slingshot.common.constants.model.ModelFileTypeConstants;

import de.uka.ipd.sdq.workflow.launchconfig.ImageRegistryHelper;
import de.uka.ipd.sdq.workflow.launchconfig.LaunchConfigPlugin;
import de.uka.ipd.sdq.workflow.launchconfig.tabs.TabHelper;

public class SimulationArchitectureModelsTab extends AbstractLaunchConfigurationTab {

	/** The id of this plug-in. */
	public static final String PLUGIN_ID = "org.palladiosimulator.analyzer.workflow";
	/** The path to the image file for the tab icon. */
	private static final String FILENAME_TAB_IMAGE_PATH = "icons/filenames_tab.gif";
	
	/** The field for path to the allocation file. */
	private Text textAllocation;
	
	/** The field for path to the usage file. */
	private Text textUsage;
//	private Text textMonitorRepository;
	
	
	private Composite container;
	private ModifyListener modifyListener;
	
	
	@Override
	public void createControl(final Composite parent) {
		modifyListener = new ModifyListener() {
			@Override
			public void modifyText(final ModifyEvent e) {
				setDirty(true);
				updateLaunchConfigurationDialog();
			}
		};
		container = new Composite(parent, SWT.NONE);
		setControl(container);
		container.setLayout(new GridLayout());
		
		
		textAllocation = new Text(container, SWT.SINGLE | SWT.BORDER);
		TabHelper.createFileInputSection(container, modifyListener, "Allocation File"
				, ModelFileTypeConstants.ALLOCATION_FILE_EXTENSION, textAllocation, "Select Allocation File", getShell(), ModelFileTypeConstants.EMPTY_STRING);
		textUsage = new Text(container, SWT.SINGLE | SWT.BORDER);
		TabHelper.createFileInputSection(container, modifyListener, "Usage File"
				, ModelFileTypeConstants.USAGEMODEL_FILE_EXTENSION, textUsage, "Select Usage File", getShell(), ModelFileTypeConstants.EMPTY_STRING);
//		textMonitorRepository = new Text(container, SWT.SINGLE | SWT.BORDER);
//		TabHelper.createFileInputSection(container, modifyListener, "MonitorRepository File"
//				, ModelFileTypeConstants.MONITOR_REPOSITORY_EXTENSION, textMonitorRepository, "Select MonitorRepository File", getShell(), ModelFileTypeConstants.EMPTY_STRING);
		
	}

	@Override
	public void setDefaults(final ILaunchConfigurationWorkingCopy configuration) {
	}

	@Override
	public void initializeFrom(final ILaunchConfiguration configuration) {
		try {
			textAllocation.setText(configuration.getAttribute(ModelFileTypeConstants.ALLOCATION_FILE, ModelFileTypeConstants.EMPTY_STRING));
		} catch (final CoreException e) {
			LaunchConfigPlugin.errorLogger(getName(),"Allocation File", e.getMessage());
		}
		try {
			textUsage.setText(configuration.getAttribute(ModelFileTypeConstants.USAGE_FILE, ModelFileTypeConstants.EMPTY_STRING));
		} catch (final CoreException e) {
			LaunchConfigPlugin.errorLogger(getName(),"Usage File", e.getMessage());
		}
//		try {
//			textMonitorRepository.setText(configuration.getAttribute(ModelFileTypeConstants.MONITOR_REPOSITORY_FILE, ModelFileTypeConstants.EMPTY_STRING));
//		} catch (CoreException e) {
//			LaunchConfigPlugin.errorLogger(getName(),"MonitorRepository File", e.getMessage());
//		}
		
	}

	@Override
	public void performApply(final ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(ModelFileTypeConstants.ALLOCATION_FILE, textAllocation.getText());
		configuration.setAttribute(ModelFileTypeConstants.USAGE_FILE, textUsage.getText());
//		configuration.setAttribute(ModelFileTypeConstants.MONITOR_REPOSITORY_FILE, textMonitorRepository.getText());
	}
	
	
	@Override
	public boolean isValid(final ILaunchConfiguration launchConfig){
		setErrorMessage(null);

		if (!TabHelper.validateFilenameExtension(textAllocation.getText(), ModelFileTypeConstants.ALLOCATION_FILE_EXTENSION)) {
			setErrorMessage("Allocation is missing.");
			return false;
		}
		if (!TabHelper.validateFilenameExtension(textUsage.getText(), ModelFileTypeConstants.USAGEMODEL_FILE_EXTENSION)) {
			setErrorMessage("Usage is missing.");
			return false;
		}
//		if (!TabHelper.validateFilenameExtension(textMonitorRepository.getText(), ModelFileTypeConstants.MONITOR_REPOSITORY_EXTENSION)) {
//			setErrorMessage("Monitor Repository is missing.");
//			return false;
//		}
		return true;
	}
	
	@Override
	public Image getImage() {
		return ImageRegistryHelper.getTabImage(PLUGIN_ID,FILENAME_TAB_IMAGE_PATH);
	}

	@Override
	public String getName() {
		return "Architecture Model(s)";
	}
	
	@Override
	public String getId() {
		return "org.palladiosimulator.analyzer.performability.ui.workflow.config.PerformabilityArchitectureModelsTab";
	}
}
