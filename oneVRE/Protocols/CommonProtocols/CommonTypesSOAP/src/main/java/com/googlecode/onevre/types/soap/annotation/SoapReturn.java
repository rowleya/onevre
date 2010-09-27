package com.googlecode.onevre.types.soap.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Tobias M Schiebeck
 *
 */
@Retention (RetentionPolicy.RUNTIME)
public @interface SoapReturn {

    /** The name of the returned value*/
    String name();

    /** The SOAP type of the returned value if it is unset it will be worked out from the Java type */
    String type() default "n/a";
}
