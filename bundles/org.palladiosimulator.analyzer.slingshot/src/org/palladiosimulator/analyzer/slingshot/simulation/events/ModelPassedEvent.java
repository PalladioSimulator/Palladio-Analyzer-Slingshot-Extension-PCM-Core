package org.palladiosimulator.analyzer.slingshot.simulation.events;

import org.eclipse.emf.ecore.EObject;

public class ModelPassedEvent<M extends EObject> extends AbstractGenericEvent<M, M> {

	public ModelPassedEvent(final M modelElement) {
		super(modelElement.getClass(), modelElement, 0.0);
	}

	/**
	 * Returns the captured model element. Synonym for {@link #getEntity()}.
	 * 
	 * @return the non-{@code null} element.
	 * @see #getEntity()
	 */
	public M getModelElement() {
		return this.getEntity();
	}
}
