package org.palladiosimulator.analyzer.slingshot.monitor.util;

import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.repository.ProvidedRole;
import org.palladiosimulator.pcm.repository.Signature;

import de.uka.ipd.sdq.simucomframework.Context;

public class AssemblyProvidedOperationPassedEvent<R extends ProvidedRole, S extends Signature>
		extends ModelElementPassedEvent<R> {

	private final AssemblyContext assemblyContext;
	private final S signature;

	public AssemblyProvidedOperationPassedEvent(final R modelElement, final EventType eventType, final Context context,
			final S signature, final AssemblyContext ofAssemblyContext) {
		super(modelElement, eventType, context);
		this.assemblyContext = ofAssemblyContext;
		this.signature = signature;
	}

	/**
	 * @return the assemblyContext
	 */
	public AssemblyContext getAssemblyContext() {
		return this.assemblyContext;
	}

	/**
	 * @return the signature
	 */
	public S getSignature() {
		return this.signature;
	}

}
