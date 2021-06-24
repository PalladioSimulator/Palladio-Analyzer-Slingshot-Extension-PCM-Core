package org.palladiosimulator.analyzer.slingshot.monitor.probe;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;

public final class ProbeRegistry {

	private final Map<Class<? extends DESEvent>, SingletonProbeInstanceMap> eventToProbeMap;

	public ProbeRegistry() {
		this.eventToProbeMap = new HashMap<>();
	}

	/**
	 * Creates a new instance of type {@code probeType} for the event {@code event}
	 * and returns it. The instance is a singleton. If such an instance already
	 * exists, then this instance is returned and no new one is created.
	 * 
	 * @param event     The type of the event from which the probe should be
	 *                  created.
	 * @param probeType The type of the probe whose instance should be returned.
	 * @param mapper
	 * @return The (possibly new) singleton instance of type {@code probeType}.
	 */
	public DESEventProbe<?, ?, ?> createProbe(final Class<? extends DESEvent> event,
			final Class<? extends DESEventProbe<?, ?, ?>> probeType, final EventToRequestContextMapper mapper) {
		if (!this.eventToProbeMap.containsKey(event)) {
			this.eventToProbeMap.put(event, new SingletonProbeInstanceMap(event));
		}

		return this.eventToProbeMap.get(event).getInstance(probeType, mapper);
	}

	public SingletonProbeInstanceMap getProbeMapFor(final Class<? extends DESEvent> event) {
		return this.eventToProbeMap.get(event);
	}

	/**
	 * Helper class that maps the concrete subtype of {@link DESEventProbe} to its
	 * actual instance. In that way, only singletons will be hold for each
	 * {@link DESEvent}.
	 * 
	 * @author Julijan Katic
	 */
	public static final class SingletonProbeInstanceMap {

		/** Maps each type to a singleton instance. */
		private final Map<Class<? extends DESEventProbe<?, ?, ?>>, DESEventProbe<?, ?, ?>> singletonInstances;

		/** The event for which the map is. */
		private final Class<? extends DESEvent> mappedEventClass;

		private SingletonProbeInstanceMap(final Class<? extends DESEvent> mappedEventClass) {
			this.singletonInstances = new HashMap<>();
			this.mappedEventClass = mappedEventClass;
		}

		/**
		 * Returns the instance of the provided type. The instance is a singleton,
		 * meaning that if such an instance of that type already exists, then that
		 * instance is returned. Otherwise, a new instance is created.
		 * <p>
		 * Keep in mind that the event probes should ONLY have public constructor with a
		 * single parameter of type {@code Class<? extends DESEvent>} (where the
		 * wildcard can be concretizised if necessary).
		 * 
		 * @param eventProbeType The type of the DESEventProbe whose instance should be
		 *                       returned.
		 * @return The instance of type {@code eventProbeType}.
		 */
		public DESEventProbe<?, ?, ?> getInstance(final Class<? extends DESEventProbe<?, ?, ?>> eventProbeType,
				final EventToRequestContextMapper mapper) {
			if (!this.getSingletonInstances().containsKey(eventProbeType)) {
				try {
					final DESEventProbe<?, ?, ?> instance = eventProbeType
							.getConstructor(this.mappedEventClass.getClass(), EventToRequestContextMapper.class)
							.newInstance(this.mappedEventClass, mapper);
					this.getSingletonInstances().put(eventProbeType, instance);
				} catch (final NoSuchMethodException e) {
					throw new RuntimeException(
							"The DESEventProbe does not have a public constructor with a single parameter of type Class<T> (where T is either a subtype of DESEvent or '? extends DESEvent')",
							e);
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException | SecurityException e) {
					// TODO Auto-generated catch block
					throw new RuntimeException(e);
				}
			}

			return this.getSingletonInstances().get(eventProbeType);
		}

		public Map<Class<? extends DESEventProbe<?, ?, ?>>, DESEventProbe<?, ?, ?>> getSingletonInstances() {
			return this.singletonInstances;
		}

		@Override
		public int hashCode() {
			return Objects.hash(this.mappedEventClass);
		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj) {
				return true;
			}
			if (!(obj instanceof SingletonProbeInstanceMap)) {
				return false;
			}
			final SingletonProbeInstanceMap other = (SingletonProbeInstanceMap) obj;
			return Objects.equals(this.mappedEventClass, other.mappedEventClass);
		}

	}

}
