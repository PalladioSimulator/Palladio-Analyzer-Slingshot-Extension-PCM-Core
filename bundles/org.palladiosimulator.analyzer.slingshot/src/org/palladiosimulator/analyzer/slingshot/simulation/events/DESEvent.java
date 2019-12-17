package org.palladiosimulator.analyzer.slingshot.simulation.events;
import java.util.List;

public interface DESEvent {

	String getId();
	List<DESEvent> handle();
	double getDelay();
}
