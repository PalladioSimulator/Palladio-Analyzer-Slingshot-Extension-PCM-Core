package org.palladiosimulator.analyzer.slingshot.behavior.annotationprocessor;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleAnnotationValueVisitor8;

import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.EventCardinality;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.OnEvent;

/**
 * The visitor for the {@link OnEvent} annotation.
 * 
 * @author Julijan Katic
 */
public class OnEventValueVisitor extends SimpleAnnotationValueVisitor8<Void, OnEventModel> {

	/**
	 * This visits every {@link OnEvent#when()} value that returns a
	 * {@code Class<?>}, and sets it in the OnEventModel appropriately.
	 * 
	 * @param p The model where the data is placed.
	 */
	@Override
	public Void visitType(final TypeMirror t, final OnEventModel p) {
		if (t.getKind() == TypeKind.DECLARED) {
			final DeclaredType classType = (DeclaredType) t;
			p.setWhenElement((TypeElement) classType.asElement());
		}
		return super.visitType(t, p);
	}

	/**
	 * This visits the {@link OnEvent#cardinality()} value returning the enum
	 * constant and sets it in the OnEventModel appropriately.
	 */
	@Override
	public Void visitEnumConstant(final VariableElement c, final OnEventModel p) {
		final EventCardinality cardinality = EventCardinality.valueOf(c.getSimpleName().toString());
		p.setCardinality(cardinality);
		return super.visitEnumConstant(c, p);
	}

	/**
	 * This visits the {@link OnEvent#then()} value returning the value of classes.
	 */
	@Override
	public Void visitArray(final List<? extends AnnotationValue> vals, final OnEventModel p) {
		final List<TypeElement> classElements = new ArrayList<>();

		for (final AnnotationValue value : vals) {
			if (value.getValue() instanceof TypeMirror) {
				final TypeMirror mirror = (TypeMirror) value.getValue();
				if (mirror.getKind() == TypeKind.DECLARED) {
					final DeclaredType classMirror = (DeclaredType) mirror;
					classElements.add((TypeElement) classMirror.asElement());
				}
			}
		}

		p.setThenElements(classElements);
		return super.visitArray(vals, p);
	}

}
