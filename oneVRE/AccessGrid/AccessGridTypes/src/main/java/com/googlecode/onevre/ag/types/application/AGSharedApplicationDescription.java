/*
 * @(#)AGSharedApplicationDescription.java
 * Created: 21-Apr-2008
 * Version: 1.0
 * Copyright (c) 2005-2006, University of Manchester All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution. Neither the name of the University of
 * Manchester nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.googlecode.onevre.ag.types.application;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.googlecode.onevre.types.soap.interfaces.SoapSerializable;



/**
 * A PAG Shared Application Description
 *
 * @author Tobias M Schiebeck
 * @version 1.0
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class AGSharedApplicationDescription implements SoapSerializable {

    private static final String[] SOAP_FIELDS =
        new String[]{"name",
                     "description",
                     "uri",
                     "mimeType",
                     "packageName"};

    private static final String[] SOAP_TYPES =
        new String[]{STRING_TYPE,
                     STRING_TYPE,
                     STRING_TYPE,
                     STRING_TYPE,
                     STRING_TYPE};

    // The name of the SharedApplication
    private String name = null;

    // The description of the SharedApplication
    private String description = null;

    // The uri of the SharedApplication
    private String uri = null;

    // The mimeType of the SharedApplication
    private String mimeType = null;

    // The name of the package
    private String packageName = null;

    /**
     * Returns the mimeType
     * @return the mimeType
     */
    @XmlElement
    public String getMimeType() {
        return mimeType;
    }

    /**
     * Returns the name
     * @return the name
     */
    @XmlElement
    public String getName() {
        return name;
    }

    /**
     * Returns the descripton
     * @return the descripton
     */
    @XmlElement
    public String getDescription() {
        return description;
    }

    /**
     * Returns the uri
     * @return the uri
     */
    @XmlElement
    public String getUri() {
        return uri;
    }

    /**
     * Sets the mimeType
     * @param mimeType
     */
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    /**
     * Sets the name
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the descripton
     * @param description The description
     */
    public void setDescription(String description) {
        this.description = description;
    }

   /**
     * Sets the uri
     * @param uri The uri
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * Sets the package name
     * @param packageName The package name
     */
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    /**
     * Gets the package name
     * @return The package name
     */
    @XmlElement
    public String getPackageName() {
        return packageName;
    }

    /**
     * Returns the SOAP type of the item
     * <dl><dt><b>overrides:</b></dt><dd>{@link ag3.soap.SoapSerializable#getSoapType()}</dd></dl>
     * @return the SOAP Type - "AGSharedApplicationDescription"
     */
    public String getSoapType() {
        return "AGSharedApplicationDescription";
    }

    /**
     * Returns the namespace of the item
     * <dl><dt><b>overrides:</b></dt><dd>{@link ag3.soap.SoapSerializable#getNameSpace()}</dd></dl>
     * @return the namespace - "http://www.accessgrid.org/v3.0"
     */
    public String getNameSpace() {
        return "http://www.accessgrid.org/v3.0";
    }

    /**
     * Returns the fields that should be included with the soap Each of the fields should have a getter and a setter with the same name e.g. field is "test" there should be a "getTest" and a "setTest" method (note standard capitalisation)
     * <dl><dt><b>overrides:</b></dt><dd>{@link ag3.soap.SoapSerializable#getFields()}</dd></dl>
     * @return the fields :
     *	<ul>
     *	<li>"name"</li>
     *	<li>"description"</li>
     *	<li>"uri"</li>
     *	<li>"mimeType"</li>
     *	<li>"packageName"</li>
     *	</ul>
     */
    public String[] getFields() {
        return SOAP_FIELDS;
    }

    /**
     * Returns the types of the fields that should be included with the soap<br>
     * If the field is not a vector or array each of the types must be one of:
     * <ol><li>A fully qualified url</li>
     * <li>A standard XML type starting with xsd:</li>
     * <li>Null if the field is itself SoapSerializable</li></ol>
     * If the return type is an array or vector, the type must be one of:
     * <ol><li>A type as above if all the values have the same type</li>
     * <li>A Vector of types if the field is a vector with different types</li></ol>
     * <dl><dt><b>overrides:</b></dt><dd>{@link ag3.soap.SoapSerializable#getTypes()}</dd></dl>
     * @return the types :
     * <ul>
     * <li>STRING_TYPE (name)</li>
     * <li>STRING_TYPE (description)</li>
     * <li>STRING_TYPE (uri)</li>
     * <li>STRING_TYPE (mimeType)</li>
     * <li>STRING_TYPE (packageName)</li>
     * </ul>
     */
    public Object[] getTypes() {
        return SOAP_TYPES;
    }

   /**
     * Returns a string representation of the object
     * @return "(&lt;ApplicationName&gt;, &lt;ApplicationURI&gt;)"
     */
    public String toString() {
        return "AGSharedApplicationDescription(" + name + ", " + uri + ")";
    }

}
