package org.visallo.webster.annotations;

import org.visallo.webster.resultWriters.ResultWriterBase;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used by {@link ResultWriterBase} to write the content type to the response.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ContentType {
    String value();
}
