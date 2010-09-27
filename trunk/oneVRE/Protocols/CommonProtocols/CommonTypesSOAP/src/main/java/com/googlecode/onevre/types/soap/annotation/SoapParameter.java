package com.googlecode.onevre.types.soap.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Tobias M Schiebeck
 *
 */
@Retention (RetentionPolicy.RUNTIME)
public @interface SoapParameter {

    /** The name of the returned value*/
    String value();
}
