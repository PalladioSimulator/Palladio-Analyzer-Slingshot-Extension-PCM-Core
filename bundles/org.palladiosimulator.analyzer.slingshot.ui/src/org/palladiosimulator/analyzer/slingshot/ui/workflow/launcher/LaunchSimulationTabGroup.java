package org.palladiosimulator.analyzer.slingshot.ui.workflow.launcher;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;

public class LaunchSimulationTabGroup extends AbstractLaunchConfigurationTabGroup {
	
	public LaunchSimulationTabGroup() {}

	@Override
	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {

		// Assemble the tab pages:
        ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] { 
        		new SimulationArchitectureModelsTab()
        		, new CommonTab()
        };
		
		setTabs(tabs);
	}

}
