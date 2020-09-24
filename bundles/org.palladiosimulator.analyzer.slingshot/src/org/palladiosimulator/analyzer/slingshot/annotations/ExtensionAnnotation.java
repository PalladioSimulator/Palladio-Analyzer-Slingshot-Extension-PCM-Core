package org.palladiosimulator.analyzer.slingshot.annotations;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * This annotation is used for marking another annotation as a "extension annotation" and can
 * define a shortcut for defining an extension. Instead of using {@link Extension}-Annotation,
 * one can use the defined ExtensionAnnotation.
 * 
 * @author Julijan Katic
 */
@Documented
@Retention(SOURCE)
@Target(ANNOTATION_TYPE)
public @interface ExtensionAnnotation {
	
	/**
	 * Defines the extension and maps it to the extension point. Typically,
	 * the {@link Extension#implementation()} is left to its default value,
	 * which indicates that the corresponding implementation is the class
	 * onto which the defined annotation is annotated.
	 */
	Extension extensionPoint();
	
	/** 
	 * The class that needs to be implemented/extended from. It is used solely
	 * for compile-time validation purposes (and IDE support) and can be left
	 * out.
	 */
	Class<?> mustBaseOn() default Object.class;
	
	/**
	 * This is used in order to map a given annotation value to the
	 * method 
	 * 
	 * @author Julijan Katic
	 */
	@Documented
	@Retention(SOURCE)
	public @interface ExtensionMapper {
		
		
		
	}
}
