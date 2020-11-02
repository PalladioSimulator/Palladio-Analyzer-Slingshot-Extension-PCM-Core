import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.OnEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.results.ResultEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import events.*;
import com.google.common.eventbus.Subscribe;

@OnEvent(when = SampleEventA.class, then = {SampleEventB.class, DESEvent.class})
public class ThenEventHasDESEvent {
	
	@Subscribe
	public ResultEvent<DESEvent> onSampleEventA(final SampleEventA sampleEventA) {
		return null; // In these tests, method implementation are not important.
	}
	
}