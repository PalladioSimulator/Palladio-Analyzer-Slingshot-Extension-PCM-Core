package org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl;

public class UsageSimulationImplTest {

//	private UsageModelRepository usageModelRepository;
//	private UsageModel usageModel;
//	private UsageSimulationImpl usageSimulationImpl; 
//	private SimulatedUserProvider provider;
//	
//	private final int WORKLOAD_POPULATION = 2;
//	
//	private SimulationDriver mockedSimulationDriver;
//
//	
//	@Before
//	public void setUp() throws Exception {
//		final Path testModelPath = Paths.get(TestHelperConstants.TEST_MODEL_BASE_PATH + "closedWorkloadWithDelay.usagemodel");
//		usageModel = UsageModelTestHelper.createUsageModelFromFile(testModelPath);
//
//		final ClosedWorkload workload = (ClosedWorkload) usageModel.getUsageScenario_UsageModel().get(0).getWorkload_UsageScenario();
//		workload.setPopulation(WORKLOAD_POPULATION);
//		
//		usageModelRepository = new UsageModelRepositoryImpl();
//		usageModelRepository.load(usageModel);
//		
//		provider = new SimulatedUserProvider();
//		provider.initializeRepository(usageModelRepository);
//		
//		usageSimulationImpl = new UsageSimulationImpl(usageModelRepository, provider);
//		
//		mockedSimulationDriver = Mockito.mock(SimulationDriver.class);
//		Mockito.doAnswer((i) -> {
//			System.out.println("SimulationScheduling got invoked");
//			return null;
//		}).when(mockedSimulationDriver).scheduleForSimulation(Mockito.anyList());
//		
//	}
//
//	
//	
//	@Ignore
//	public void testOnUserFinished() {
////		this needs to be fetched from the workload ... 
////		SimulatedUser finshedUser = new SimulatedUser();
////		FinishUserEvent finishUserEvent = new FinishUserEvent();
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testOnSimulationStarted() {
//		// initial state: evt, mocked engine
//		// JUNIT 4 -> JUNIT 5 (uses lambda stuff)  
//		final SimulationStarted mySimStartEvt = new SimulationStarted();
////		usageSimulationImpl.init(usageModel);
//		
//		// call the onSimulation Start		
////		ManyEvents<UserStarted> initialUserRequests = usageSimulationImpl.onSimulationStart(mySimStartEvt);
//				
//		// the number of events scheduled should be the same as the workload population		
////		assertEquals(initialUserRequests.getManyEvents().size(), WORKLOAD_POPULATION);
//	}
//
//	@Ignore
//	public void testOnUserWokeUp() {
//		
//	}

}
