package org.palladiosimulator.analyzer.slingshot.scalingpolicy.interpreter.utils;

import java.util.Iterator;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature.Setting;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.EcoreUtil.Copier;
import org.eclipse.emf.ecore.util.InternalEList;
import org.palladiosimulator.edp2.models.measuringpoint.MeasuringPoint;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.resourceenvironment.LinkingResource;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;

import de.uka.ipd.sdq.identifier.IdentifierPackage;

/**
 * This class handles the special copying of a {@link ResourceContainer}. It also copies the {@link AssemblyContext},
 * {@link LinkingResource} and {@link MeasuringPoint}.
 * 
 * @author Julijan Katic
 *
 */
public class ResourceContainerCopier extends Copier {
	
	public ResourceContainerCopier() {
		super(true, false); // Don't use original references, but resolve proxies.
	}

	@Override
	protected void copyAttributeValue(final EAttribute eAttribute, final EObject eObject, 
			final Object value, final Setting setting) {
		final Object newValue;
		if (eAttribute == IdentifierPackage.Literals.IDENTIFIER__ID) {
			newValue = EcoreUtil.generateUUID();
		} else {
			newValue = value;
		}
		
		super.copyAttributeValue(eAttribute, eObject, newValue, setting);
	}
	
	
	
	@Override
	protected void copyReference(EReference eReference, EObject eObject, EObject copyEObject) {
		if (!eObject.eIsSet(eReference)) {
			// The reference isn't set anyway.
			return;
		}
		
		if (eReference.isMany()) {
			final InternalEList<EObject> source = (InternalEList<EObject>) eObject.eGet(eReference);
			final InternalEList<EObject> target = (InternalEList<EObject>) copyEObject.eGet(getTarget(eReference));
			
			if (source.isEmpty()) {
				target.clear();
			} else {
				final boolean isBidirectional = eReference.getEOpposite() != null;
				int index = 0;
				final Iterator<EObject> iterator = resolveProxies ? source.iterator() : source.basicIterator();
				
				while (iterator.hasNext()) {
					final EObject referencedEObject = iterator.next();
					final EObject copyReferencedEObject = get(referencedEObject);
					final boolean needOriginal = useOriginalReferences || (referencedEObject instanceof LinkingResource);
					
					if (copyReferencedEObject == null) {
						if (needOriginal && !isBidirectional) {
							target.addUnique(index, referencedEObject);
							++index;
						}
						
						// If it is a AssemblyContext, copy that
						if (referencedEObject instanceof AssemblyContext) {
							final AssemblyContext copy = ResourceContainerCopier.copy((AssemblyContext) referencedEObject);
							target.addUnique(index, copy);
							++index;
						}
					} else {
						if (isBidirectional) {
							final int position = target.indexOf(copyReferencedEObject);
							if (position == -1) {
								target.addUnique(index, copyReferencedEObject);
							} else if (index != position) {
								target.move(index, copyReferencedEObject);
							}
						} else {
							target.addUnique(index, copyReferencedEObject);
						}
						++index;
					} // copyReferencedEObject == null
				} // iterator
			} // source.isEmpty()
		} else {
			final Object referencedEObject = eObject.eGet(eReference, resolveProxies);
			if (referencedEObject == null) {
				copyEObject.eSet(getTarget(eReference), null);
			} else {
				final Object copyReferencedEObject = get(referencedEObject);
				if (copyReferencedEObject == null) {
					final boolean needOriginal = useOriginalReferences || (referencedEObject instanceof LinkingResource);
					if (needOriginal && eReference.getEOpposite() == null) {
						copyEObject.eSet(getTarget(eReference), referencedEObject);
					}
					
					if (referencedEObject instanceof AssemblyContext) {
						final AssemblyContext copy = ResourceContainerCopier.copy((AssemblyContext) referencedEObject);
						copyEObject.eSet(getTarget(eReference), copy);
					}
				} else {
					copyEObject.eSet(getTarget(eReference), copyReferencedEObject);
				}
			}
		}
	}

	public static ResourceContainer copy(final ResourceContainer eObject) {
		final Copier copier = new ResourceContainerCopier();
		final ResourceContainer result = (ResourceContainer) copier.copy(eObject);
		copier.copyReferences();
		return result;
	}
	
	public static AssemblyContext copy(final AssemblyContext eObject) {
		final Copier copier = new Copier();
		final AssemblyContext result = (AssemblyContext) copier.copy(eObject);
		copier.copyReferences();
		return result;
	}
}
