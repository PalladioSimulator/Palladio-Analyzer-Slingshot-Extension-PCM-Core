package org.palladiosimulator.analyzer.slingshot.simulation.core.extensions;
import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.simulation.core.SimulationBehaviourExtension;

public class ExtensionLoggingInterceptor extends AbstractInterceptor {

	
	private final Logger LOGGER = Logger.getLogger(ExtensionLoggingInterceptor.class);

	public ExtensionLoggingInterceptor() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void preIntercept(final Object extension, final Method m, final Object[] args) {
		
		LOGGER.info("Pre-intercept of the method: "+m.getName());

		
	}
	
	@Override
	public void postIntercept(final Object extension, final Method m, final Object[] args,final Object result) {
		LOGGER.info("Post-intercept of the method: " + m.getName());
	}


	

}
