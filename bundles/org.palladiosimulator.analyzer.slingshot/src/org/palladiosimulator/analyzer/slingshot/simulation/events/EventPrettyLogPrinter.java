package org.palladiosimulator.analyzer.slingshot.simulation.events;

public class EventPrettyLogPrinter {

	public static String prettyPrint(DESEvent evt) {
		return "Event [" + evt.getClass().getSimpleName() + ", " + evt.getId() + "]";
	}
	
	public static String prettyPrint(DESEvent evt, String msg, String component) {
		return "Event[" + evt.getClass().getSimpleName() + ", " + evt.getId() + "]|[" + component + "] " + msg + "";
	}
	
	public static String prettyPrint(DESEvent evt, String msg, String component) {
		return "Event[" + evt.getClass().getSimpleName() + ", " + evt.getId() + "]|[" + component + "] " + msg + "";
	}
}
