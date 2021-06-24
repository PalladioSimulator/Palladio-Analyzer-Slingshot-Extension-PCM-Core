package org.palladiosimulator.analyzer.slingshot.event;

import org.palladiosimulator.analyzer.slingshot.simulation.api.EventDispatcher;

import com.google.inject.AbstractModule;

public final class EventDispatcherModule extends AbstractModule {

	@Override
	protected void configure() {
		super.configure();
		this.bind(EventDispatcher.class).to(GuavaBasedEventDispatcher.class);
	}

}
