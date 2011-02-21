/*
 * @(#)AGNetworkServiceDescription.java
 * Created: 21-May-2009
 * Version: 1.0
 * Copyright (c) 2005-2009, University of Manchester All rights reserved.
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

import java.util.Vector;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


import com.googlecode.onevre.ag.types.Capability;
import com.googlecode.onevre.types.soap.interfaces.SoapSerializable;




/**
 * An AGTk AG Network Service Description
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class AGNetworkServiceDescription implements SoapSerializable {

    private static final String[] SOAP_FIELDS =
        new String[]{"name",
                     "description",
                     "uri",
                     "version",
                     "capabilities"};

    private static final String[] SOAP_TYPES =
        new String[]{STRING_TYPE,
                     STRING_TYPE,
                     STRING_TYPE,
                     STRING_TYPE,
                     null};

    // The name of the service
    private String name = null;

    // The description of the service
    private String description = null;

    // The uri of the service
    private String uri = null;

    // The version of the service
    private String version = null;

    // The capabilities of the service
    private Vector<Capability> capabilities = new Vector<Capability>();


    /**
     * Returns the capabilities
     * @return the capabilities
     */
    @XmlElement
    public Vector<Capability> getCapabilities() {
        return capabilities;
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
     * Returns the description
     * @return the description
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
     * Adds a capability
     * @param capability The capability to add
     */
    public void setCapabilities(Capability capability) {
        this.capabilities.add(capability);
    }

    /**
     * Sets the capabilities
     * @param capabilities the capabilities
     */
    public void setCapabilities(Vector<Capability> capabilities) {
        this.capabilities = capabilities;
    }

    /**
     * Sets the name
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the description
     * @param description The description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Sets the uri
     * @param uri The service uri
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * Sets the version
     * @param version The version of the service
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Gets the package name
     * @return The package name
     */
    @XmlElement
    public String getVersion() {
        return version;
    }

    /**
     * Returns the SOAP type of the item
     * <dl><dt><b>overrides:</b></dt><dd>{@link ag3.soap.SoapSerializable#getSoapType()}</dd></dl>
     * @return the SOAP Type - "AGNetworkServiceDescription"
     */
    public String getSoapType() {
        return "AGNetworkServiceDescription";
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
     *	<ul><li>"name"</li><li>"uri"</li><li>"capabilities"</li><li>"packageName"</li></ul>
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
     * <li>STRING_TYPE (uri)</li>
     * <li>null (capabilities) -> {@link ag3.interfaces.types.Capability}</li>
     * <li>STRING_TYPE (packageName)</li>
     * </ul>
     */
    public Object[] getTypes() {
        return SOAP_TYPES;
    }

    /**
     * Returns a string representation of the object
     * @return "AGNetworkServiceDescription(&lt;ServiceName&gt;, &lt;ServiceURI&gt;)"
     */
    public String toString() {
        return "AGNerworkServiceDescription(" + name + ", " + uri + ")";
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object other) {
        AGNetworkServiceDescription onsd = (AGNetworkServiceDescription) other;
        return uri.equals(onsd.uri);
    }

    public int hashCode() {
        return uri.hashCode();
    }

}
