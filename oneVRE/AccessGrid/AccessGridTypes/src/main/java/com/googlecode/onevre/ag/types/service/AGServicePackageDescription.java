/*
 * @(#)ServiceDescription.java
 * Created: 01-Aug-2007
 * Version: 1.0
 *
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

package com.googlecode.onevre.ag.types.service;

import com.googlecode.onevre.types.soap.interfaces.SoapSerializable;


/**
 * The AG3 Service Package Description
 * @author Anja Le Blanc
 * @version 1.0
 */
public class AGServicePackageDescription implements SoapSerializable {

    private static final String[] SOAP_FIELDS =
        new String[]{"name",
                     "description",
                     "launchUrl",
                     "packageName",
                     "serviceClass"};

    private static final String[] SOAP_TYPES =
        new String[]{STRING_TYPE,
                     STRING_TYPE,
                     STRING_TYPE,
                     STRING_TYPE,
                     STRING_TYPE};


    // The name of the service
    private String name = null;

    // The description of the service
    private String description = null;

    // The url used to launch the service
    private String launchUrl = null;

    // The directory holding the service on the server
    private String packageName = null;

    // The class name of the Service to be run
    private String serviceClass = null;


    /**
     * Returns the SOAP type of the item
     * <dl><dt><b>overrides:</b></dt><dd>{@link ag3.soap.SoapSerializable#getSoapType()}</dd></dl>
     * @return the SOAP Type - "AGServicePackageDescription"
     */
    public String getSoapType() {
        return "AGServicePackageDescription";
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
     *	<li>"launchUrl"</li>
     *	<li>"packageName"</li>
     *	<li>"serviceClass"</li>
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
     * <ul><li>STRING_TYPE (name)</li><li>STRING_TYPE (description)</li><li>STRING_TYPE (launchUrl)</li><li>STRING_TYPE (packageName)</li><li>STRING_TYPE (serviceClass)</li></ul>
     */
    public Object[] getTypes() {
        return SOAP_TYPES;
    }

    /**
     * Gets the name of the service
     * @return The name of the service
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the service
     * @param name The name of the service
     */
    public void setName(String name) {
        this.name = name;
    }


    /**
     * Sets the description of the service
     * @param description The description of the service
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the description
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the launchUrl
     * @return the launchUrl
     */
    public String getLaunchUrl() {
        return launchUrl;
    }

    /**
     * Sets the launchUrl
     * @param launchUrl the launchUrl to set
     */
    public void setLaunchUrl(String launchUrl) {
        this.launchUrl = launchUrl;
    }

    /**
     * Returns the packageName
     * @return the packageName
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * Sets the packageName
     * @param packageName the packageName to set
     */
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

	/**
	 * @param serviceClass the serviceClass to set
	 */
	public void setServiceClass(String serviceClass) {
		this.serviceClass = serviceClass;
	}

	/**
	 * @return the serviceClass
	 */
	public String getServiceClass() {
		return serviceClass;
	}

}
