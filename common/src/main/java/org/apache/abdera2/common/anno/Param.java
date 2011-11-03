package org.apache.abdera2.common.anno;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Used with the Context Annotation to establish a static context 
 * primarily for use with the URI Template implementation
 */
@Retention(RUNTIME)
@Target( {TYPE})
public @interface Param {
  String name();
  String value();
}
