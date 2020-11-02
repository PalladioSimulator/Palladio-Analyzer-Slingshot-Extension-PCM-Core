import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.OnEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.results.ResultEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import events.*;
import com.google.common.eventbus.Subscribe;

@OnEvent(when = DESEvent.class, then = SampleEventB.class)
public class WhenEventIsDESEvent {
	
	/* Should miss the contract: SampleEventB instead of SampleEventA. */
	@Subscribe
	public ResultEvent<DESEvent> onSampleEventA(final DESEvent sampleEventA) {
		return ResultEvent.of(new SampleEventB());
	}
	
}