package org.palladiosimulator.analyzer.slingshot.simulation.core.extensions;

import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.annotations.EventCardinality;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.annotations.OnEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.results.ResultEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;

//FIXME:: checkCardinality what is the meaning of checking the cardinality at runtime do we need it at all?
public class EventContractChecker {

	ContractResult checkCardinality(ResultEvent<DESEvent> outputEvent, OnEvent onEventContract) {
		
		boolean failed = false;
		String msg = "Contract Check Successful";
		
		
		if(outputEvent.isEmpty()) {
			
			
//			if (!onEventContract.cardinality().equals(EventCardinality.SINGLE)) {
//				failed = true;
//				msg = "Extension Method Is Not Returning the Cardinality According to Contract, Returned: Many Events, Contract: " + onEventContract.cardinality().toString();
//			}
		}
		if(outputEvent.areMany()) {
			if (!onEventContract.cardinality().equals(EventCardinality.MANY)) {
				failed = true;
				msg = "Extension Method Is Not Returning the Cardinality According to Contract, Returned: Many Events, Contract: " + onEventContract.cardinality().toString();
			}
		}

		return new ContractResult(failed, msg);
				
	}

	ContractResult checkEventType(ResultEvent<DESEvent> outputResultEvent, OnEvent onEventContract) {
		boolean failed = false;
		String msg = "Contract Check Successful";
		
		
		Class<? extends DESEvent>[] outputClazz = onEventContract.outputEventType();
//		
//		for (DESEvent event : outputResultEvent.getEventsForScheduling()) {
//			try {
//				outputClazz.cast(event);
//			}catch(ClassCastException ex) {
//				failed = true;
//				msg = "The EventResult contains events of a type which is not as the one in the contract: " + outputClazz.getCanonicalName();
//			}
//		}
		
		return new ContractResult(failed, msg);
	}
}
