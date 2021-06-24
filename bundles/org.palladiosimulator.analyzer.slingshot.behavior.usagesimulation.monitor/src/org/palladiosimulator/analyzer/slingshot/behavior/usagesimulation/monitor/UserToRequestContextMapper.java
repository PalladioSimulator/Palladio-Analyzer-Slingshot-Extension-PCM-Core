package org.palladiosimulator.analyzer.slingshot.behavior.usagesimulation.monitor;

import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.User;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.AbstractUserChangedEvent;
import org.palladiosimulator.analyzer.slingshot.monitor.probe.EventToRequestContextMapper;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.probeframework.measurement.RequestContext;

public class UserToRequestContextMapper implements EventToRequestContextMapper {

	@Override
	public RequestContext mapFrom(final DESEvent event) {
		if (event instanceof AbstractUserChangedEvent) {
			final AbstractUserChangedEvent userChangedEvent = (AbstractUserChangedEvent) event;
			final User user = userChangedEvent.getEntity().getUser();
			return new RequestContext(user.getId());
		}
		return RequestContext.EMPTY_REQUEST_CONTEXT;
	}

}
