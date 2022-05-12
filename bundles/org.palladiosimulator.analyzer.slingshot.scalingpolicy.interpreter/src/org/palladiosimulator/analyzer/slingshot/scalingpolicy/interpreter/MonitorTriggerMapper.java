package org.palladiosimulator.analyzer.slingshot.scalingpolicy.interpreter;

import java.util.HashMap;
import java.util.Map;

import org.palladiosimulator.analyzer.slingshot.monitor.data.MeasurementMade;
import org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.TriggerContext;

/**
 * This class maps a certain Monitor/Measuring Point to a Trigger Context.
 * In this way, it is possible to react on a certain {@link MeasurementMade} event
 * when needed.
 * 
 * TODO: Point this to the measuringpoint instead of monitor
 * 
 * @author Julijan Katic
 *
 */
public final class MonitorTriggerMapper {
	
	private final Map<String, TriggerContext> monitorTriggerMap = new HashMap<>();
	
	public void put(final String monitorId, final TriggerContext triggerContext) {
		this.monitorTriggerMap.put(monitorId, triggerContext);
	}
	
	public TriggerContext get(final String monitorId) {
		return this.monitorTriggerMap.get(monitorId);
	}
	
	
}
