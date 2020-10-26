package org.palladiosimulator.analyzer.slingshot.annotationprocessor.util.exceptions;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic.Kind;

public class ProcessException extends Exception {

	private final Element element;

	public ProcessException(final Element element, final String msg) {
		super(msg);
		this.element = element;
	}

	public Element getElement() {
		return element;
	}

	/**
	 * Convenience method to show this message on a certain messager.
	 * 
	 * @param messager The messager onto which to show the error.
	 */
	public void showError(final Messager messager) {
		messager.printMessage(Kind.ERROR, this.getMessage(), this.getElement());
	}
}
