package org.palladiosimulator.analyzer.slingshot.simulation.core.extensions;

import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.annotations.EventCardinality;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.annotations.OnEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.results.ManyEvents;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.results.OptionalEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.results.SingleEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;

public class EventContractChecker {

	ContractResult checkCardinality(Object outputEvent, OnEvent onEventContract) {
		
		boolean failed = false;
		String msg = "Contract Check Successful";
		
		if (outputEvent instanceof ManyEvents) {
			if (!onEventContract.cardinality().equals(EventCardinality.MANY)) {
				failed = true;
				msg = "Extension Method Is Not Returning the Cardinality According to Contract, Returned: Many Events, Contract: " + onEventContract.cardinality().toString();
			}

		} else if (outputEvent instanceof SingleEvent) {
			if (!onEventContract.cardinality().equals(EventCardinality.SINGLE)) {
				failed = true;
				msg = "Extension Method Is Not Returning the Cardinality According to Contract, Returned: Single, Contract: " + onEventContract.cardinality().toString();
			}

		} else if (outputEvent instanceof OptionalEvent) {
			if (!onEventContract.cardinality().equals(EventCardinality.SINGLE)) {
				failed = true;
				msg = "Extension Method Is Not Returning the Cardinality According to Contract, Returned: OptionalEvent with Cardinality Single, Contract: " + onEventContract.cardinality().toString();
			}
		}
		return new ContractResult(failed, msg);
				
	}

	ContractResult checkEventType(Object outputEvent, OnEvent onEventContract) {
		
		// outputEvent -> ManyEvents<UserStarted>
		// outputType -> UserStarted
		
		
		boolean failed = false;
		String msg = "Contract Check Successful";
		
		
		Class<? extends DESEvent> outputType = onEventContract.outputEventType();
		
		// outputType -> SimulationStarted.class
		// outputEvent instanceof ManyEvents -> true
		// outputEvent instanceof UserStarted -> false
		
		//TODO:: ManyEvents is a container object that has instances of DESEvents, 
		// we need to check that those instances are of type outputType as declared in the contract.
		
		// SimulationStop -> DESEvent
		// UserStarted -> DESEvent
		
		
		if (outputEvent instanceof ManyEvents) {
	
			ManyEvents<DESEvent> outputEventCasted = (ManyEvents) outputEvent; 
			
//			if(outputEventCasted.getEventsForScheduling().toArray()[0] instanceof outputType) {
//				// 
//			}
//			
//			outputType.cast(obj)
			// signle element of this cotainer check instanceof outputType? 
//	
			
			
			
		} else if (outputEvent instanceof SingleEvent) {
	
		} else if (outputEvent instanceof OptionalEvent) {
			
		}
		
		
		
		if (!outputEvent.getClass().equals(outputType)) {
			failed = true;
			msg = "Extension has returned a type of event which is not declared in the contract";
		}
		
		return new ContractResult(failed, msg);

	}
}
