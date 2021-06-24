package org.palladiosimulator.analyzer.slingshot.simulation.api;

import java.util.List;

import org.palladiosimulator.analyzer.slingshot.simulation.core.exceptions.EventContractException;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.EventContract;

/**
 * An interface that is responsible of how to schedule the events. This also
 * checks whether the event contracts ({@link EventContract}) are hold.
 * 
 * @author Julijan Katic
 */
public interface SimulationScheduling {

	/**
	 * Schedules a single event if its contract holds. This will call
	 * {@link #checkEventContract(DESEvent)} internally. Only after this check and
	 * only if the check is successful, then the event will be scheduled.
	 * 
	 * @param event The event onto which the contract will be checked and, if it
	 *              holds, then be published for scheduling.
	 */
	void scheduleForSimulation(DESEvent event);

	/**
	 * Convenience method for scheduling multiple, ordered events at the same time.
	 * For each event in {@code events}, the contract of
	 * {@link #scheduleForSimulation(DESEvent)} holds.
	 * 
	 * @param events An ordered list of events.
	 * @see #scheduleForSimulation(DESEvent)
	 */
	void scheduleForSimulation(List<DESEvent> events);

	/**
	 * Checks whether the event contract holds of {@code event}. If no contract has
	 * been specified, then this should be handled as if the contract holds.
	 * 
	 * @param event The event having a contract. If no contract is present, then the
	 *              check results in true.
	 * @throws EventContractException If the contract has been violated.
	 */
	void checkEventContract(DESEvent event) throws EventContractException;
}
