package org.palladiosimulator.slingshot.tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.palladiosimulator.slingshot.User;
import org.palladiosimulator.slingshot.simulation.SimulationDriver;
import org.palladiosimulator.slingshot.simulation.events.IEvent;

class ExampleClassTest {

	private SimulationDriver driver;

	@Ignore
	void testRetrieveNextRequestFromUser() {
	
		User user = null;
		IEvent request = driver.findSuccessorRequest(user);
		
		assertNotNull(request);
		
	}

}
