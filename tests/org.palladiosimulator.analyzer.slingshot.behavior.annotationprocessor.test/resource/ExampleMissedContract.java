import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.OnEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.results.ResultEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import events.*;
import com.google.common.eventbus.Subscribe;

@OnEvent(when = SampleEventA.class, then = SampleEventB.class)
public class ExampleMissedContract {
	
	/* Should miss the contract: SampleEventB instead of SampleEventA. */
	@Subscribe
	public ResultEvent<DESEvent> onSampleEventA(final SampleEventB sampleEventB) {
		return ResultEvent.of(new SampleEventB());
	}
	
}